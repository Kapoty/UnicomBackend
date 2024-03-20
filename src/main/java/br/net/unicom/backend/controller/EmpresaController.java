package br.net.unicom.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.net.unicom.backend.model.Cargo;
import br.net.unicom.backend.model.Contrato;
import br.net.unicom.backend.model.Departamento;
import br.net.unicom.backend.model.Empresa;
import br.net.unicom.backend.model.Equipe;
import br.net.unicom.backend.model.Papel;
import br.net.unicom.backend.model.Permissao;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.payload.response.UsuarioResponse;
import br.net.unicom.backend.repository.CargoRepository;
import br.net.unicom.backend.repository.ContratoRepository;
import br.net.unicom.backend.repository.DepartamentoRepository;
import br.net.unicom.backend.repository.EmpresaRepository;
import br.net.unicom.backend.repository.EquipeRepository;
import br.net.unicom.backend.repository.JornadaRepository;
import br.net.unicom.backend.repository.PapelRepository;
import br.net.unicom.backend.repository.PermissaoRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.EmpresaService;
import br.net.unicom.backend.service.UsuarioService;
import jakarta.validation.Valid;




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
    EmpresaService empresaService;

    @Autowired
    ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('Empresa.Read.All')")
    @GetMapping("")
    public ResponseEntity<List<Empresa>> getAll() {
        return new ResponseEntity<List<Empresa>>(empresaRepository.findAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('Empresa.Read.All')")
    @GetMapping("/{empresaId}")
    public ResponseEntity<Empresa> getEmpresaByEmpresaId(@Valid @PathVariable("empresaId") Integer empresaId) {
        return ResponseEntity.of(empresaRepository.findByEmpresaId(empresaId));
    }

    @PreAuthorize("hasAuthority('Empresa.Read.All')")
    @GetMapping("/{empresaId}/permissao")
    public ResponseEntity<List<Permissao>> getPermissaoListByEmpresaId(@Valid @PathVariable("empresaId") Integer empresaId) {
        return ResponseEntity.ok(permissaoRepository.findAllByEmpresaId(empresaId));
    }
    
    @PreAuthorize("hasAuthority('Empresa.Read.All')")
    @GetMapping("/{empresaId}/papel")
    public ResponseEntity<List<Papel>> getPapelListByEmpresaId(@Valid @PathVariable("empresaId") Integer empresaId) {
        return ResponseEntity.ok(papelRepository.findAllByEmpresaId(empresaId));
    }

    @PreAuthorize("hasAuthority('Empresa.Read.All')")
    @GetMapping("/{empresaId}/usuario")
    public ResponseEntity<List<Usuario>> getUsuarioListByEmpresaId(@Valid @PathVariable("empresaId") Integer empresaId) {
        return ResponseEntity.ok(usuarioRepository.findAllByEmpresaId(empresaId));
    }

    @PreAuthorize("hasAuthority('Usuario.Read.All')")
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

    @PreAuthorize("hasAuthority('Usuario.Read.All')")
    @GetMapping("/me/papel")
    public ResponseEntity<List<Papel>> getPapelListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(papelRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }

    @PreAuthorize("hasAuthority('Usuario.Read.All')")
    @GetMapping("/me/cargo")
    public ResponseEntity<List<Cargo>> getCargoListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(cargoRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }

    @PreAuthorize("hasAuthority('Usuario.Read.All')")
    @GetMapping("/me/contrato")
    public ResponseEntity<List<Contrato>> getContratoListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(contratoRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }

    @PreAuthorize("hasAuthority('Usuario.Read.All')")
    @GetMapping("/me/departamento")
    public ResponseEntity<List<Departamento>> getDepartamentoListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(departamentoRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }

    @PreAuthorize("hasAuthority('Usuario.Read.All')")
    @GetMapping("/me/equipe")
    public ResponseEntity<List<Equipe>> getEquipeListByEmpresaMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(equipeRepository.findAllByEmpresaId(userDetails.getEmpresaId()));
    }
}