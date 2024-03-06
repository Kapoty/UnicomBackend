package br.net.unicom.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.net.unicom.backend.model.Permissao;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.repository.PermissaoRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/usuario",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PermissaoRepository permissaoRepository;

    @GetMapping("")
    public ResponseEntity<List<Usuario>> getAll() {
        return new ResponseEntity<List<Usuario>>(usuarioRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<Usuario> getUsuarioByEmpresaIdAndUsuarioId(@Valid @PathVariable("usuarioId") Integer usuarioId) {
        return ResponseEntity.of(usuarioRepository.findByUsuarioId(usuarioId));
    }

    @GetMapping("/{usuarioId}/permissao")
    public ResponseEntity<List<Permissao>> getPermissoesByEmpresaIdAndUsuarioId(@Valid @PathVariable("usuarioId") Integer usuarioId) {
        return ResponseEntity.ok(permissaoRepository.findAllByUsuarioId(usuarioId));
    }

    @GetMapping("/me")
    public ResponseEntity<Usuario> getUsuarioByMe() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.of(usuarioRepository.findByEmail(userDetails.getUsername()));
    }

}
