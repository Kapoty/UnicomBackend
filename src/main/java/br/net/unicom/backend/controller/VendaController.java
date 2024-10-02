package br.net.unicom.backend.controller;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import br.net.unicom.backend.model.FiltroVenda;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.Venda;
import br.net.unicom.backend.model.VendaAtualizacao;
import br.net.unicom.backend.model.VendaFatura;
import br.net.unicom.backend.model.VendaProduto;
import br.net.unicom.backend.model.VendaProdutoPortabilidade;
import br.net.unicom.backend.model.VendaStatus;
import br.net.unicom.backend.model.VendaSuporte;
import br.net.unicom.backend.model.enums.VendaStatusCategoriaEnum;
import br.net.unicom.backend.model.exception.FieldInvalidException;
import br.net.unicom.backend.model.projection.VendaAtoresProjection;
import br.net.unicom.backend.payload.request.VendaAtualizacaoListRequest;
import br.net.unicom.backend.payload.request.VendaFaturaRequest;
import br.net.unicom.backend.payload.request.VendaFindByOsOrCustcodeOrOrdemRequest;
import br.net.unicom.backend.payload.request.VendaListRequest;
import br.net.unicom.backend.payload.request.VendaPatchFaturaListRequest;
import br.net.unicom.backend.payload.request.VendaPatchRequest;
import br.net.unicom.backend.payload.request.VendaPostRequest;
import br.net.unicom.backend.payload.request.VendaProdutoPortabilidadeRequest;
import br.net.unicom.backend.payload.request.VendaProdutoRequest;
import br.net.unicom.backend.payload.request.VendaSuporteRequest;
import br.net.unicom.backend.repository.FiltroVendaRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.repository.VendaAtualizacaoRepository;
import br.net.unicom.backend.repository.VendaProdutoRepository;
import br.net.unicom.backend.repository.VendaRepository;
import br.net.unicom.backend.repository.VendaStatusRepository;
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
    VendaAtualizacaoRepository vendaAtualizacaoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    VendaProdutoRepository vendaProdutoRepository;

    @Autowired
    FiltroVendaRepository filtroVendaRepository;

    @Autowired
    VendaStatusRepository vendaStatusRepository;

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

        // Carregar atores da venda ja filtradas

        Boolean verTodasVendas = userDetails.hasAuthority("VER_TODAS_VENDAS");
        List<Integer> usuarioIdList = new ArrayList<>();

        if (!verTodasVendas)
            //usuarioIdList = usuarioService.getUsuarioListLessThanUsuario(usuario, true).stream().map(u -> u.getUsuarioId()).collect(Collectors.toList());
            usuarioIdList = Collections.singletonList(usuario.getUsuarioId());

        List<VendaAtoresProjection> vendaAtoresList = vendaRepository.findAllByEmpresaIdAndFiltersAndUsuarioIdList(
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
            vendaListRequest.getLimit(),
            verTodasVendas,
            usuarioIdList
        );

        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        logger.info("2: " + String.valueOf(timeElapsed));

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
    @PostMapping("/find-by-os-or-custcode-or-ordem")
    public ResponseEntity<Venda> findByOsOrCustCodeOrOrdem(@Valid @RequestBody VendaFindByOsOrCustcodeOrOrdemRequest vendaFindByOsOrCustcodeOrOrdemRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).get();

        Venda venda = vendaRepository.findByOsOrCustcodeOrOrdemAndEmpresaId(vendaFindByOsOrCustcodeOrOrdemRequest.getOs(),
            vendaFindByOsOrCustcodeOrOrdemRequest.getCustcode(),
            vendaFindByOsOrCustcodeOrOrdemRequest.getOrdem(),
            userDetails.getEmpresaId()).get();

        if (!userDetails.hasAuthority("VER_TODAS_VENDAS") && !vendaService.usuarioPodeVerVenda(usuario , venda))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(venda);
    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS') && hasAuthority('ALTERAR_AUDITOR') && hasAuthority('AUTOMACOES')")
    @PatchMapping("/{vendaId}/faturaList")
    @Transactional
    public ResponseEntity<Void> patchFaturaListByVendaId(@Valid @PathVariable("vendaId") Integer vendaId, @Valid @RequestBody VendaPatchFaturaListRequest vendaPatchFaturaListRequest) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).get();

        Venda venda = vendaRepository.findByVendaIdAndEmpresaId(vendaId, userDetails.getEmpresaId()).orElseThrow(NoSuchElementException::new);

        // verificar se o usuário tem permissão para alterar a venda

        // pode ver a venda
        if (!userDetails.hasAuthority("VER_TODAS_VENDAS") && !vendaService.usuarioPodeVerVenda(usuario , venda))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        
        // jackson
        
        HashMap<String, String> before = jsonService.flatten(venda, Venda.WithProdutoAndFaturaListView.class);
    
        // criar faturas

        List<VendaFatura> faturaList = new ArrayList<>();

        for (int faturaId = 1; faturaId <= vendaPatchFaturaListRequest.getFaturaList().size(); faturaId++) {
            VendaFaturaRequest faturaRequest = vendaPatchFaturaListRequest.getFaturaList().get(faturaId - 1);

            VendaFatura fatura;
            if (venda.getFaturaList().size() >= faturaId)
                fatura = venda.getFaturaList().get(faturaId - 1);
            else
                fatura = new VendaFatura(venda, faturaId);

            modelMapper.map(faturaRequest, fatura);
            
            faturaList.add(fatura);
        }

        venda.setFaturaList(faturaList);

        vendaRepository.saveAndFlush(venda);

        // jackson

        HashMap<String, String> after = jsonService.flatten(venda, Venda.WithProdutoAndFaturaListView.class);

        String difference = jsonService.difference(before, after);

        vendaService.novaAtualizacao(usuario, venda, vendaPatchFaturaListRequest.getRelato() + " (automação)", difference);

        return ResponseEntity.noContent().build();

    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @PatchMapping("/{vendaId}")
    @Transactional
    public ResponseEntity<Void> patchByVendaId(@Valid @PathVariable("vendaId") Integer vendaId, @Valid @RequestBody VendaPatchRequest vendaPatchRequest) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).get();

        Venda venda = vendaRepository.findByVendaIdAndEmpresaId(vendaId, userDetails.getEmpresaId()).orElseThrow(NoSuchElementException::new);

        // verificar se o usuário tem permissão para alterar a venda

        // pode ver a venda
        if (!userDetails.hasAuthority("VER_TODAS_VENDAS") && !vendaService.usuarioPodeVerVenda(usuario , venda))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        VendaStatus novoStatus = vendaStatusRepository.findByVendaStatusId(vendaPatchRequest.getStatusId()).get();

        // pode alterar venda com status em pos_venda
        if (!userDetails.hasAuthority("ALTERAR_AUDITOR") && (venda.getStatus().getCategoria().equals(VendaStatusCategoriaEnum.POS_VENDA) || novoStatus.getCategoria().equals(VendaStatusCategoriaEnum.POS_VENDA)))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        
        // jackson
        
        HashMap<String, String> before = jsonService.flatten(venda, Venda.WithProdutoAndFaturaListView.class);

        // mapear atributos

        modelMapper.map(vendaPatchRequest, venda);

        // mapear atributos com permissao ALTERAR_AUDITOR

        if (userDetails.hasAuthority("ALTERAR_AUDITOR")) {
            venda.setOs(vendaPatchRequest.getOs());
            venda.setCustcode(vendaPatchRequest.getCustcode());
            venda.setOrdem(vendaPatchRequest.getOrdem());
            venda.setReimpute(vendaPatchRequest.getReimpute());
            venda.setAnulada(vendaPatchRequest.getAnulada());
            venda.setDataInstalacao(vendaPatchRequest.getDataInstalacao());
            venda.setVendaOriginal(vendaPatchRequest.getVendaOriginal());
            venda.setBrscan(vendaPatchRequest.getBrscan());
            venda.setSuporte(vendaPatchRequest.getSuporte());
            venda.setLoginVendedor(vendaPatchRequest.getLoginVendedor());
        }

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

        if (userDetails.hasAuthority("ALTERAR_AUDITOR")) {

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

        }

        // criar suportes

        if (userDetails.hasAuthority("ALTERAR_AUDITOR")) {

            List<VendaSuporte> suporteList = new ArrayList<>();

            for (int suporteId = 1; suporteId <= vendaPatchRequest.getSuporteList().size(); suporteId++) {
                VendaSuporteRequest suporteRequest = vendaPatchRequest.getSuporteList().get(suporteId - 1);

                VendaSuporte suporte;
                if (venda.getSuporteList().size() >= suporteId)
                    suporte = venda.getSuporteList().get(suporteId - 1);
                else
                    suporte = new VendaSuporte(venda, suporteId);

                modelMapper.map(suporteRequest, suporte);
                
                suporteList.add(suporte);
            }

            venda.setSuporteList(suporteList);

        }

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
            venda.setAgenteBiometriaId(vendaPatchRequest.getAgenteBiometriaId());
            venda.setAgenteSuporteId(vendaPatchRequest.getAgenteSuporteId());
            venda.setAuditorExterno(vendaPatchRequest.getAuditorExterno());
            venda.setCadastradorExterno(vendaPatchRequest.getCadastradorExterno());
            venda.setAgenteBiometriaExterno(vendaPatchRequest.getAgenteBiometriaExterno());
            venda.setAgenteSuporteExterno(vendaPatchRequest.getAgenteSuporteExterno());
            venda.setDataVenda(vendaPatchRequest.getDataVenda());
            venda.setDataAgendamento(vendaPatchRequest.getDataAgendamento());
        }

        vendaRepository.saveAndFlush(venda);

        // jackson

        HashMap<String, String> after = jsonService.flatten(venda, Venda.WithProdutoAndFaturaListView.class);

        String difference = jsonService.difference(before, after);

        vendaService.novaAtualizacao(usuario, venda, vendaPatchRequest.getRelato(), difference);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @PostMapping("/")
    @Transactional
    public ResponseEntity<Venda> postVenda(@Valid @RequestBody VendaPostRequest vendaPostRequest) throws FieldInvalidException {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).get();

        LocalDateTime agora = LocalDateTime.now();

        // verificar se o usuário tem permissão para alterar a venda

        VendaStatus novoStatus = vendaStatusRepository.findByVendaStatusId(vendaPostRequest.getStatusId()).get();

        // pode alterar venda com status em pos_venda
        if (!userDetails.hasAuthority("ALTERAR_AUDITOR") && novoStatus.getCategoria().equals(VendaStatusCategoriaEnum.POS_VENDA))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        // mapear atributos

        Venda venda = modelMapper.map(vendaPostRequest, Venda.class);

        // mapear atributos com permissao ALTERAR_AUDITOR

        if (userDetails.hasAuthority("ALTERAR_AUDITOR")) {
            venda.setOs(vendaPostRequest.getOs());
            venda.setCustcode(vendaPostRequest.getCustcode());
            venda.setOrdem(vendaPostRequest.getOrdem());
            venda.setReimpute(vendaPostRequest.getReimpute());
            venda.setAnulada(vendaPostRequest.getAnulada());
            venda.setDataInstalacao(vendaPostRequest.getDataInstalacao());
            venda.setVendaOriginal(vendaPostRequest.getVendaOriginal());
            venda.setBrscan(vendaPostRequest.getBrscan());
            venda.setSuporte(vendaPostRequest.getSuporte());
            venda.setLoginVendedor(vendaPostRequest.getLoginVendedor());
            venda.setDataAgendamento(vendaPostRequest.getDataAgendamento());
        } else {
            venda.setOs("");
            venda.setCustcode("");
            venda.setOrdem("");
            venda.setReimpute(false);
            venda.setAnulada(false);
            venda.setDataInstalacao(null);
            venda.setVendaOriginal(true);
            venda.setBrscan(null);
            venda.setSuporte(null);
            venda.setLoginVendedor("");
        }

        // definir empresAId

        venda.setEmpresaId(userDetails.getEmpresaId());

        // definir vendedor/supervisor/auditor
        // alterar vendedor/supervisor/auditor/cadastrador se houver permissao

        if (vendaPostRequest.getVendedorId() != null) {
            Usuario usuarioFilho = usuarioRepository.findByUsuarioId(vendaPostRequest.getVendedorId()).get();

            if (!usuarioFilho.equals(usuario) && !userDetails.hasAuthority("ALTERAR_VENDEDOR") && !usuarioService.isUsuarioGreaterThan(usuario, usuarioFilho))
                throw new FieldInvalidException("vendedorId", "vendedor inválido");

            venda.setVendedorId(vendaPostRequest.getVendedorId());
            venda.setVendedor(usuarioFilho);

            if (vendaPostRequest.getSupervisorId() == null) {
                if (venda.getVendedor().getEquipe() != null)
                    venda.setSupervisorId(venda.getVendedor().getEquipe().getSupervisorId());
                else
                    venda.setSupervisorId(usuario.getUsuarioId());
            }
        }

        venda.setVendedorExterno(vendaPostRequest.getVendedorExterno());

        if (userDetails.hasAuthority("ALTERAR_VENDEDOR")) {

            if (vendaPostRequest.getSupervisorId() != null)
                venda.setSupervisorId(vendaPostRequest.getSupervisorId());

            venda.setSupervisorExterno(vendaPostRequest.getSupervisorExterno());
        }
        
        if (userDetails.hasAuthority("ALTERAR_AUDITOR")) {
            venda.setAuditorId(vendaPostRequest.getAuditorId());
            venda.setCadastradorId(vendaPostRequest.getCadastradorId());
            venda.setAgenteBiometriaId(vendaPostRequest.getAgenteBiometriaId());
            venda.setAgenteSuporteId(vendaPostRequest.getAgenteSuporteId());
            venda.setAuditorExterno(vendaPostRequest.getAuditorExterno());
            venda.setCadastradorExterno(vendaPostRequest.getCadastradorExterno());
            venda.setAgenteBiometriaExterno(vendaPostRequest.getAgenteBiometriaExterno());
            venda.setAgenteSuporteExterno(vendaPostRequest.getAgenteSuporteExterno());
        }

        // definir dataCadastro dataVenda dataStatus statusId

        venda.setDataStatus(agora);

        venda.setDataCadastro(agora);

        if (userDetails.hasAuthority("ALTERAR_AUDITOR")) {
            venda.setDataVenda(Optional.ofNullable(vendaPostRequest.getDataVenda()).orElse(agora));
        } else {
            venda.setDataVenda(agora);
        }

        // salvar venda para obter vendaId

        //logger.info(venda.toString());

        vendaRepository.saveAndFlush(venda);

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

        if (userDetails.hasAuthority("ALTERAR_AUDITOR")) {

            List<VendaFatura> faturaList = new ArrayList<>();

            for (int faturaId = 1; faturaId <= vendaPostRequest.getFaturaList().size(); faturaId++) {
                VendaFaturaRequest faturaRequest = vendaPostRequest.getFaturaList().get(faturaId - 1);

                VendaFatura fatura = new VendaFatura(venda, faturaId);

                modelMapper.map(faturaRequest, fatura);
                
                faturaList.add(fatura);
            }

            venda.setFaturaList(faturaList);
        
        }

        // criar suportes

        if (userDetails.hasAuthority("ALTERAR_AUDITOR")) {

            List<VendaSuporte> suporteList = new ArrayList<>();

            for (int suporteId = 1; suporteId <= vendaPostRequest.getSuporteList().size(); suporteId++) {
                VendaSuporteRequest suporteRequest = vendaPostRequest.getSuporteList().get(suporteId - 1);

                VendaSuporte suporte = new VendaSuporte(venda, suporteId);

                modelMapper.map(suporteRequest, suporte);
                
                suporteList.add(suporte);
            }

            venda.setSuporteList(suporteList);
        
        }

        // salvar venda novamente

        vendaRepository.saveAndFlush(venda);

        vendaService.novaAtualizacao(usuario, venda, vendaPostRequest.getRelato());

        return ResponseEntity.status(HttpStatus.CREATED).body(venda);
    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @PostMapping("/venda-atualizacao-list")
    public ResponseEntity<List<VendaAtualizacao>> getVendaAtualizacaoList(@Valid @RequestBody VendaAtualizacaoListRequest vendaAtualizacaoListRequest) {

        logger.info(vendaAtualizacaoListRequest.toString());

        Instant start = Instant.now();
        Instant finish;
        long timeElapsed;

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).get();

        // Carregar atores da venda ja filtradas

        Boolean verTodasVendas = userDetails.hasAuthority("VER_TODAS_VENDAS");
        List<Integer> usuarioIdList = new ArrayList<>();

        if (!verTodasVendas)
            usuarioIdList = Collections.singletonList(usuario.getUsuarioId());

        LocalDateTime dataInicio = LocalDateTime.now().minus(24, ChronoUnit.HOURS);

        if (vendaAtualizacaoListRequest.getDataInicio() != null && vendaAtualizacaoListRequest.getDataInicio().isAfter(dataInicio))
            dataInicio = vendaAtualizacaoListRequest.getDataInicio();

        List<VendaAtualizacao> vendaAtualizacaoList = vendaAtualizacaoRepository.findAllByEmpresaIdAndFiltersAndUsuarioIdList(usuario.getEmpresaId(), dataInicio, verTodasVendas, usuarioIdList);

        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toMillis();
        logger.info("1: " + String.valueOf(timeElapsed));
        
        return ResponseEntity.ok(vendaAtualizacaoList);
    }
}
