package br.net.unicom.backend.controller;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.net.unicom.backend.model.FiltroVenda;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.Venda;
import br.net.unicom.backend.model.VendaAtualizacao;
import br.net.unicom.backend.model.VendaFatura;
import br.net.unicom.backend.model.VendaProduto;
import br.net.unicom.backend.model.VendaProdutoPortabilidade;
import br.net.unicom.backend.model.projection.VendaAtoresProjection;
import br.net.unicom.backend.payload.request.VendaFaturaRequest;
import br.net.unicom.backend.payload.request.VendaListRequest;
import br.net.unicom.backend.payload.request.VendaPatchRequest;
import br.net.unicom.backend.payload.request.VendaPostRequest;
import br.net.unicom.backend.payload.request.VendaProdutoPortabilidadeRequest;
import br.net.unicom.backend.payload.request.VendaProdutoRequest;
import br.net.unicom.backend.repository.FiltroVendaRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.repository.VendaProdutoRepository;
import br.net.unicom.backend.repository.VendaRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.JsonService;
import br.net.unicom.backend.service.UsuarioService;
import br.net.unicom.backend.service.VendaService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.Validator;



@RestController
@Validated
@RequestMapping(
    value = "/venda",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class VendaController {

    @Autowired
    VendaRepository vendaRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    VendaProdutoRepository vendaProdutoRepository;

    @Autowired
    FiltroVendaRepository filtroVendaRepository;

    @Autowired
    VendaService vendaService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JsonService jsonService;

    @Autowired
    Validator validator;

    @Autowired
    EntityManager entityManager;

    Logger logger = LoggerFactory.getLogger(VendaController.class);

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @PostMapping("/venda-list")
    @Transactional
    @JsonView(Venda.WithProdutoAndFaturaListView.class)
    public ResponseEntity<List<Venda>> getVendaList(@Valid @RequestBody VendaListRequest vendaListRequest) {

        logger.info(vendaListRequest.toString());

        Instant start = Instant.now();
        Instant finish;
        long timeElapsed;

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).get();

        // Salvar filtro venda do usuario

        FiltroVenda filtroVenda = filtroVendaRepository.findByUsuarioId(usuario.getUsuarioId()).orElseGet(() -> new FiltroVenda(usuario));

        modelMapper.map(vendaListRequest, filtroVenda);
        filtroVenda.setStatusIdList(vendaListRequest.getStatusIdList().stream().map(String::valueOf).collect(Collectors.joining(",")));
        filtroVendaRepository.save(filtroVenda);

        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        logger.info("1: " + String.valueOf(timeElapsed));

        // Carregar atores da venda

        List<VendaAtoresProjection> vendaAtoresList = vendaRepository.findAllByEmpresaIdAndFilters(
            userDetails.getEmpresaId(),
            vendaListRequest.getTipoProduto() != null ? vendaListRequest.getTipoProduto().toString() : null,
            vendaListRequest.getPdv().toLowerCase(),
            vendaListRequest.getSafra(),
            vendaListRequest.getTipoData() != null ? vendaListRequest.getTipoData().toString() : "",
            vendaListRequest.getDataInicio() != null ? vendaListRequest.getDataInicio() : LocalDate.of(1990, 1, 1),
            vendaListRequest.getDataFim() != null ? vendaListRequest.getDataFim().plusDays(1) : LocalDate.of(2100, 1, 1),
            vendaListRequest.getStatusIdList(),
            vendaListRequest.getOs().toLowerCase(),
            vendaListRequest.getCpf().toLowerCase(),
            vendaListRequest.getNome().toLowerCase(),
            vendaListRequest.getOffset(),
            vendaListRequest.getLimit()
        );

        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        logger.info("2: " + String.valueOf(timeElapsed));

        // Filtrar vendas por permissão dos atores

        if (!userDetails.hasAuthority("VER_TODAS_VENDAS")) {
            Set<Integer> usuarioIdList = usuarioService.getUsuarioListLessThanUsuario(usuario).stream().map(u -> u.getUsuarioId()).collect(Collectors.toSet());
            usuarioIdList.add(usuario.getUsuarioId());

            vendaAtoresList.removeIf(vendaAtor -> {
                if (vendaAtor.getVendedorId() == null)
                    return false;
                if (usuarioIdList.contains(vendaAtor.getVendedorId()) || usuarioIdList.contains(vendaAtor.getSupervisorId()))
                    return false;
                return true;
            } );
        }

        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        logger.info("3: " + String.valueOf(timeElapsed));

        // Carregar vendas

        List<Integer> vendaIdList = vendaAtoresList.stream().map(vendaAtores -> vendaAtores.getVendaId()).collect(Collectors.toList());

        List<Venda> vendaList = vendaRepository.findAllByVendaIdIn(vendaIdList);

        vendaList.forEach(venda -> {
            Hibernate.initialize(venda.getProdutoList());
            venda.getProdutoList().forEach(produto -> Hibernate.initialize(produto.getPortabilidadeList()));
            Hibernate.initialize(venda.getFaturaList());
            //Hibernate.initialize(venda.getAtualizacaoList());
        });

        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        logger.info("4: " + String.valueOf(timeElapsed));

        //logger.info(String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)));
        
        return ResponseEntity.ok(vendaList);
    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @GetMapping("/{vendaId}")
    public ResponseEntity<Venda> getVendaByVendaId(@Valid @PathVariable("vendaId") Integer vendaId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).get();

        Venda venda = vendaRepository.findByVendaIdAndEmpresaId(vendaId, userDetails.getEmpresaId()).get();

        if (!userDetails.hasAuthority("VER_TODAS_VENDAS") && !vendaService.usuarioPodeVerVenda(usuario , venda))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(venda);
    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @PatchMapping("/{vendaId}")
    @Transactional
    public ResponseEntity<Void> patchByVendaId(@Valid @PathVariable("vendaId") Integer vendaId, @Valid @RequestBody VendaPatchRequest vendaPatchRequest) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).get();

        Venda venda = vendaRepository.findByVendaIdAndEmpresaId(vendaId, userDetails.getEmpresaId()).orElseThrow(NoSuchElementException::new);

        // jackson

        /*JsonNode node = objectMapper.valueToTree(venda);
        HashMap<String, String> before = jsonService.flatten(node, "");*/

        // mapear atributos

        modelMapper.map(vendaPatchRequest, venda);

        // atualizar dataStatus e criar atualizacao

        LocalDateTime dataStatus = LocalDateTime.now();

        venda.setDataStatus(dataStatus);

        VendaAtualizacao atualizacao = new VendaAtualizacao();
        atualizacao.setVendaId(venda.getVendaId());
        atualizacao.setStatusId(vendaPatchRequest.getStatusId());
        atualizacao.setUsuarioId(usuario.getUsuarioId());
        atualizacao.setData(dataStatus);
        atualizacao.setRelato(vendaPatchRequest.getRelato());

        venda.getAtualizacaoList().add(atualizacao);

        // criar produtos e portabilidades

        List<VendaProduto> produtoList = new ArrayList<>();

        for (int produtoId = 1; produtoId <= vendaPatchRequest.getProdutoList().size(); produtoId++) {
            VendaProdutoRequest produtoRequest = vendaPatchRequest.getProdutoList().get(produtoId - 1);

            VendaProduto produto;
            if (venda.getProdutoList().size() >= produtoId)
                produto = venda.getProdutoList().get(produtoId - 1);
            else
                produto = new VendaProduto(venda, produtoId);

            modelMapper.map(produtoRequest, produto);

            List<VendaProdutoPortabilidade> portabilidadeList = new ArrayList<>();

            for (int portabilidadeId = 1; portabilidadeId <= produtoRequest.getPortabilidadeList().size(); portabilidadeId++) {
                VendaProdutoPortabilidadeRequest vendaProdutoPortabilidadeRequest = produtoRequest.getPortabilidadeList().get(portabilidadeId - 1);

                VendaProdutoPortabilidade portabilidade;
                if (produto.getPortabilidadeList().size() >= portabilidadeId)
                    portabilidade = produto.getPortabilidadeList().get(portabilidadeId - 1);
                else
                    portabilidade = new VendaProdutoPortabilidade(produto, portabilidadeId);

                modelMapper.map(vendaProdutoPortabilidadeRequest, portabilidade);

                portabilidadeList.add(portabilidade);
            }

            produto.setPortabilidadeList(portabilidadeList);

            produtoList.add(produto);
        }

        venda.setProdutoList(produtoList);

        // criar faturas

        List<VendaFatura> faturaList = new ArrayList<>();

        for (int faturaId = 1; faturaId <= vendaPatchRequest.getFaturaList().size(); faturaId++) {
            VendaFaturaRequest faturaRequest = vendaPatchRequest.getFaturaList().get(faturaId - 1);

            VendaFatura fatura;
            if (venda.getFaturaList().size() >= faturaId)
                fatura = venda.getFaturaList().get(faturaId - 1);
            else
                fatura = new VendaFatura(venda, faturaId);

            modelMapper.map(faturaRequest, fatura);
            
            faturaList.add(fatura);
        }

        venda.setFaturaList(faturaList);

        // alterar vendedor/supervisor/auditor/cadastrador se houver permissao

        if (userDetails.hasAuthority("ALTERAR_VENDEDOR")) {
            venda.setVendedorId(vendaPatchRequest.getVendedorId());
            venda.setSupervisorId(vendaPatchRequest.getSupervisorId());
            venda.setVendedorExterno(vendaPatchRequest.getVendedorExterno());
            venda.setSupervisorExterno(vendaPatchRequest.getSupervisorExterno());
        }
        
        if (userDetails.hasAuthority("ALTERAR_AUDITOR")) {
            venda.setAuditorId(vendaPatchRequest.getAuditorId());
            venda.setCadastradorId(vendaPatchRequest.getCadastradorId());
            venda.setAuditorExterno(vendaPatchRequest.getAuditorExterno());
            venda.setCadastradorExterno(vendaPatchRequest.getCadastradorExterno());
        }

        vendaRepository.saveAndFlush(venda);

        // jackson

        /*node = objectMapper.valueToTree(venda);
        HashMap<String, String> after = jsonService.flatten(node, "");

        HashMap<String, String> difference = new HashMap<>();

        for (String key : before.keySet()) {
            if (!after.containsKey(key) || !after.get(key).equals(before.get(key)))
                difference.put(key, after.get(key));
        }

        for (String key : after.keySet()) {
            if (!before.containsKey(key))
                difference.put(key, after.get(key));
        }

        logger.info(difference.toString());*/

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @PostMapping("/")
    @Transactional
    public ResponseEntity<Venda> postVenda(@Valid @RequestBody VendaPostRequest vendaPostRequest) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).get();

        LocalDateTime agora = LocalDateTime.now();

        // mapear atributos

        Venda venda = modelMapper.map(vendaPostRequest, Venda.class);

        // definir empresAId

        venda.setEmpresaId(userDetails.getEmpresaId());

        // definir vendedor/supervisor/auditor

        venda.setVendedorId(usuario.getUsuarioId());
        if (usuario.getEquipe() != null)
            venda.setSupervisorId(usuario.getEquipe().getSupervisorId());

        // definir dataCadastro dataVenda dataStatus statusId

        venda.setDataCadastro(agora);

        venda.setDataVenda(Optional.ofNullable(vendaPostRequest.getDataVenda()).orElse(agora));

        venda.setDataStatus(agora);

        // salvar venda para obter vendaId

        //logger.info(venda.toString());

        vendaRepository.saveAndFlush(venda);

        // criar atualizacao

        VendaAtualizacao atualizacao = new VendaAtualizacao();
        atualizacao.setVendaId(venda.getVendaId());
        atualizacao.setStatusId(vendaPostRequest.getStatusId());
        atualizacao.setUsuarioId(usuario.getUsuarioId());
        atualizacao.setData(agora);
        atualizacao.setRelato(vendaPostRequest.getRelato());

        venda.getAtualizacaoList().add(atualizacao);

        // criar produtos e portabilidades

        List<VendaProduto> produtoList = new ArrayList<>();

        for (int produtoId = 1; produtoId <= vendaPostRequest.getProdutoList().size(); produtoId++) {
            VendaProdutoRequest produtoRequest = vendaPostRequest.getProdutoList().get(produtoId - 1);
            
            VendaProduto produto = new VendaProduto(venda, produtoId);

            modelMapper.map(produtoRequest, produto);

            List<VendaProdutoPortabilidade> portabilidadeList = new ArrayList<>();
            
            for (int portabilidadeId = 1; portabilidadeId <= produtoRequest.getPortabilidadeList().size(); portabilidadeId++) {
                VendaProdutoPortabilidadeRequest vendaProdutoPortabilidadeRequest = produtoRequest.getPortabilidadeList().get(portabilidadeId - 1);

                VendaProdutoPortabilidade portabilidade = new VendaProdutoPortabilidade(produto, portabilidadeId);

                modelMapper.map(vendaProdutoPortabilidadeRequest, portabilidade);

                portabilidadeList.add(portabilidade);
            }

            produto.setPortabilidadeList(portabilidadeList);

            produtoList.add(produto);
        }

        venda.setProdutoList(produtoList);

        // criar faturas

        List<VendaFatura> faturaList = new ArrayList<>();

        for (int faturaId = 1; faturaId <= vendaPostRequest.getFaturaList().size(); faturaId++) {
            VendaFaturaRequest faturaRequest = vendaPostRequest.getFaturaList().get(faturaId - 1);

            VendaFatura fatura = new VendaFatura(venda, faturaId);

            modelMapper.map(faturaRequest, fatura);
            
            faturaList.add(fatura);
        }

        venda.setFaturaList(faturaList);

        // salvar venda novamente

        vendaRepository.saveAndFlush(venda);

        return ResponseEntity.status(HttpStatus.CREATED).body(venda);
    }
}
