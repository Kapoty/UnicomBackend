package br.net.unicom.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import br.net.unicom.backend.model.Adicional;
import br.net.unicom.backend.model.Cargo;
import br.net.unicom.backend.model.Contrato;
import br.net.unicom.backend.model.Departamento;
import br.net.unicom.backend.model.Equipe;
import br.net.unicom.backend.model.JornadaStatus;
import br.net.unicom.backend.model.JornadaStatusGrupo;
import br.net.unicom.backend.model.Papel;
import br.net.unicom.backend.model.PontoDeVenda;
import br.net.unicom.backend.model.Produto;
import br.net.unicom.backend.model.Sistema;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.Venda;
import br.net.unicom.backend.model.VendaFatura;
import br.net.unicom.backend.model.VendaProduto;
import br.net.unicom.backend.model.VendaProdutoPortabilidade;
import br.net.unicom.backend.model.VendaStatus;
import br.net.unicom.backend.payload.response.EquipeResponse;
import br.net.unicom.backend.payload.response.UsuarioPBIResponse;
import br.net.unicom.backend.repository.AdicionalRepository;
import br.net.unicom.backend.repository.CargoRepository;
import br.net.unicom.backend.repository.ContratoRepository;
import br.net.unicom.backend.repository.DepartamentoRepository;
import br.net.unicom.backend.repository.EmpresaRepository;
import br.net.unicom.backend.repository.EquipeRepository;
import br.net.unicom.backend.repository.JornadaRepository;
import br.net.unicom.backend.repository.JornadaStatusGrupoRepository;
import br.net.unicom.backend.repository.JornadaStatusRepository;
import br.net.unicom.backend.repository.PapelRepository;
import br.net.unicom.backend.repository.PermissaoRepository;
import br.net.unicom.backend.repository.PontoDeVendaRepository;
import br.net.unicom.backend.repository.ProdutoRepository;
import br.net.unicom.backend.repository.SistemaRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.repository.VendaFaturaRepository;
import br.net.unicom.backend.repository.VendaProdutoPortabilidadeRepository;
import br.net.unicom.backend.repository.VendaProdutoRepository;
import br.net.unicom.backend.repository.VendaRepository;
import br.net.unicom.backend.repository.VendaStatusRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.EmpresaService;
import br.net.unicom.backend.service.EquipeService;
import br.net.unicom.backend.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;




@RestController
@Validated
@RequestMapping(
    value = "/empresa",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class EmpresaController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    EquipeService equipeService;

    @Autowired
    PermissaoRepository permissaoRepository;

    @Autowired
    PapelRepository papelRepository;

    @Autowired
    EmpresaRepository empresaRepository;

    @Autowired
    CargoRepository cargoRepository;

    @Autowired
    ContratoRepository contratoRepository;

    @Autowired
    DepartamentoRepository departamentoRepository;

    @Autowired
    JornadaRepository jornadaRepository;

    @Autowired
    EquipeRepository equipeRepository;

    @Autowired
    JornadaStatusRepository jornadaStatusRepository;

    @Autowired
    VendaStatusRepository vendaStatusRepository;

    @Autowired
    JornadaStatusGrupoRepository jornadaStatusGrupoRepository;

    @Autowired
    EmpresaService empresaService;

    @Autowired
    VendaRepository vendaRepository;

    @Autowired
    ProdutoRepository produtoRepository;

    @Autowired
    SistemaRepository sistemaRepository;

    @Autowired
    PontoDeVendaRepository pontoDeVendaRepository;

    @Autowired
    AdicionalRepository adicionalRepository;

    @Autowired
    VendaProdutoRepository vendaProdutoRepository;

    @Autowired
    VendaProdutoPortabilidadeRepository vendaProdutoPortabilidadeRepository;

    @Autowired
    VendaFaturaRepository vendaFaturaRepository;

    @Autowired
    ModelMapper modelMapper;

    Logger logger = LoggerFactory.getLogger(EmpresaController.class);

    @GetMapping("/me/usuario")
    @JsonView(Usuario.DefaultView.class)
    public ResponseEntity<List<Usuario>> getUsuarioListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Usuario> usuarioList = usuarioRepository.findAllByEmpresaId(userDetails.getEmpresaId());
        return ResponseEntity.ok(usuarioList);
    }

    @PreAuthorize("hasAuthority('POWER_BI')")
    @GetMapping("/me/pbi/usuario")
    public ResponseEntity<List<UsuarioPBIResponse>> getUsuarioListByEmpresaMeForPowerBi() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Usuario> usuarioList = usuarioRepository.findAllByEmpresaId(userDetails.getEmpresaId());

        List<UsuarioPBIResponse> usuarioPBIList = new ArrayList<>();
        usuarioList.forEach(usuario -> {
            UsuarioPBIResponse usuarioPBIResponse = modelMapper.map(usuario, UsuarioPBIResponse.class);

            String usuariosAbaixo = 
                usuarioService.getUsuarioListLessThanUsuario(usuario)
                .stream()
                .map(u -> "[" + u.getUsuarioId() + "]")
                .collect(Collectors.joining());

            if (usuariosAbaixo.indexOf("[" + usuario.getUsuarioId() + "]") < 0)
                usuariosAbaixo += "[" + usuario.getUsuarioId() + "]";

            usuarioPBIResponse.setUsuariosAbaixo(usuariosAbaixo);

            usuarioPBIResponse.setVerTodasVendas(usuarioService.getPermissaoList(usuario).contains("VER_TODAS_VENDAS"));

            usuarioPBIList.add(usuarioPBIResponse);
        });

        return ResponseEntity.ok(usuarioPBIList);
    }

    @GetMapping("/me/papel")
    public ResponseEntity<List<Papel>> getPapelListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(papelRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }

    @GetMapping("/me/cargo")
    public ResponseEntity<List<Cargo>> getCargoListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(cargoRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }

    @GetMapping("/me/contrato")
    public ResponseEntity<List<Contrato>> getContratoListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(contratoRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }

    @GetMapping("/me/departamento")
    public ResponseEntity<List<Departamento>> getDepartamentoListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(departamentoRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }

    @GetMapping("/me/jornada-status-grupo")
    public ResponseEntity<List<JornadaStatusGrupo>> getJornadaStatusGrupoListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(jornadaStatusGrupoRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }

    @GetMapping("/me/equipe")
    public ResponseEntity<List<EquipeResponse>> getEquipeListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Equipe> equipeList = equipeRepository.findAllByEmpresaId(userDetails.getEmpresaId());

        return ResponseEntity.ok(
            equipeList.stream().
                        map(equipe -> modelMapper.map(equipe, EquipeResponse.class)).
                        collect(Collectors.toList())
            );
    }

    @GetMapping("/me/jornada-status")
    public ResponseEntity<List<JornadaStatus>> getJornadaStatusListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(jornadaStatusRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }

    @GetMapping("/me/venda-status")
    public ResponseEntity<List<VendaStatus>> getVendaStatusListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(vendaStatusRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }

    @GetMapping("/me/produto")
    public ResponseEntity<List<Produto>> getProdutoListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(produtoRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }

    @GetMapping("/me/sistema")
    public ResponseEntity<List<Sistema>> getSistemaListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(sistemaRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }

    @GetMapping("/me/ponto-de-venda")
    public ResponseEntity<List<PontoDeVenda>> getPontoDeVendaListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(pontoDeVendaRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }

    @GetMapping("/me/adicional")
    public ResponseEntity<List<Adicional>> getAdicionalListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(adicionalRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }

    @PreAuthorize("hasAuthority('VER_TODAS_VENDAS')")
    @GetMapping("/me/venda")
    @JsonView(Venda.DefaultView.class)
    public ResponseEntity<List<Venda>> getVendaListByEmpresaMe(@RequestParam(defaultValue = "0") Integer offset, @Valid @RequestParam(defaultValue = "2000") @Min(1) @Max(2000) Integer limit) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(vendaRepository.findAllByEmpresaIdAndLimit(userDetails.getEmpresaId(), offset, limit));
    }

    @PreAuthorize("hasAuthority('VER_TODAS_VENDAS')")
    @GetMapping("/me/venda-produto")
    @JsonView(VendaProduto.DefaultView.class)
    public ResponseEntity<List<VendaProduto>> getVendaProdutoListByEmpresaMe(@RequestParam(defaultValue = "0") Integer offset, @Valid @RequestParam(defaultValue = "2000") @Min(1) @Max(2000)  Integer limit) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(vendaProdutoRepository.findAllByEmpresaIdAndLimit(userDetails.getEmpresaId(), offset, limit));
    }

    @PreAuthorize("hasAuthority('VER_TODAS_VENDAS')")
    @GetMapping("/me/venda-produto-portabilidade")
    public ResponseEntity<List<VendaProdutoPortabilidade>> getVendaProdutoPortabilidadeListByEmpresaMe(@RequestParam(defaultValue = "0") Integer offset, @Valid @RequestParam(defaultValue = "2000") @Min(1) @Max(2000) Integer limit) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(vendaProdutoPortabilidadeRepository.findAllByEmpresaIdAndLimit(userDetails.getEmpresaId(), offset, limit));
    }

    @PreAuthorize("hasAuthority('VER_TODAS_VENDAS')")
    @GetMapping("/me/venda-fatura")
    public ResponseEntity<List<VendaFatura>> getVendaFaturaListByEmpresaMe(@RequestParam(defaultValue = "0") Integer offset, @Valid @RequestParam(defaultValue = "2000") @Min(1) @Max(2000) Integer limit) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(vendaFaturaRepository.findAllByEmpresaIdAndLimit(userDetails.getEmpresaId(), offset, limit));
    }
}