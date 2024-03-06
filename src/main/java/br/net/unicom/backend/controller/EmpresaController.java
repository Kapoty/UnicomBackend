package br.net.unicom.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.net.unicom.backend.model.Empresa;
import br.net.unicom.backend.model.Papel;
import br.net.unicom.backend.model.Permissao;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.repository.EmpresaRepository;
import br.net.unicom.backend.repository.PapelRepository;
import br.net.unicom.backend.repository.PermissaoRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.service.EmpresaService;
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
    PermissaoRepository permissaoRepository;

    @Autowired
    PapelRepository papelRepository;

    @Autowired
    EmpresaRepository empresaRepository;

    @Autowired
    EmpresaService empresaService;

    @GetMapping("")
    public ResponseEntity<List<Empresa>> getAll() {
        return new ResponseEntity<List<Empresa>>(empresaRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{empresaId}")
    public ResponseEntity<Empresa> getEmpresaByEmpresaId(@Valid @PathVariable("empresaId") Integer empresaId) {
        return ResponseEntity.of(empresaRepository.findByEmpresaId(empresaId));
    }

    @GetMapping("/{empresaId}/permissao")
    public ResponseEntity<List<Permissao>> getPermissoesByEmpresaId(@Valid @PathVariable("empresaId") Integer empresaId) {
        return ResponseEntity.ok(permissaoRepository.findAllByEmpresaId(empresaId));
    }

    @GetMapping("/{empresaId}/papel")
    public ResponseEntity<List<Papel>> getPapeisByEmpresaId(@Valid @PathVariable("empresaId") Integer empresaId) {
        return ResponseEntity.ok(papelRepository.findAllByEmpresaId(empresaId));
    }

    @GetMapping("/{empresaId}/usuario")
    public ResponseEntity<List<Usuario>> getUsuariosByEmpresaId(@Valid @PathVariable("empresaId") Integer empresaId) {
        return ResponseEntity.ok(usuarioRepository.findAllByEmpresaId(empresaId));
    }

    /*@PostMapping("/{empresaId}/usuario")
    public String createUsuarioByEmpresaId(@Valid @RequestBody CreateUsuarioRequest createUsuarioRequest) {
        return "";
    }*/
    
}