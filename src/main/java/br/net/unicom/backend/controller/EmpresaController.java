package br.net.unicom.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.net.unicom.backend.model.Cargo;
import br.net.unicom.backend.model.Contrato;
import br.net.unicom.backend.model.Departamento;
import br.net.unicom.backend.model.Equipe;
import br.net.unicom.backend.model.JornadaStatus;
import br.net.unicom.backend.model.JornadaStatusGrupo;
import br.net.unicom.backend.model.Papel;
import br.net.unicom.backend.model.Produto;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.VendaStatus;
import br.net.unicom.backend.payload.response.EquipeResponse;
import br.net.unicom.backend.payload.response.UsuarioResponse;
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
import br.net.unicom.backend.repository.ProdutoRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.repository.VendaRepository;
import br.net.unicom.backend.repository.VendaStatusRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.EmpresaService;
import br.net.unicom.backend.service.EquipeService;
import br.net.unicom.backend.service.UsuarioService;




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
    ModelMapper modelMapper;

    @GetMapping("/me/usuario")
    public ResponseEntity<List<UsuarioResponse>> getUsuarioListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Usuario> usuarioList = usuarioRepository.findAllByEmpresaId(userDetails.getEmpresaId());
        return ResponseEntity.ok(
            usuarioList.stream().
                        map(usuario -> usuarioService.usuarioToUsuarioResponse(usuario)).
                        collect(Collectors.toList())
            );
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
                        map(equipe -> equipeService.equipeToEquipeResponse(equipe)).
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
}