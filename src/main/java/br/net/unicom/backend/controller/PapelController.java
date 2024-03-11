package br.net.unicom.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.net.unicom.backend.model.Papel;
import br.net.unicom.backend.model.Permissao;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.repository.PapelRepository;
import br.net.unicom.backend.repository.PermissaoRepository;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/papel",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class PapelController {

    @Autowired
    PermissaoRepository permissaoRepository;

    @Autowired
    PapelRepository papelRepository;

    @PreAuthorize("hasAuthority('Papel.Read.All')")
    @GetMapping("")
    public ResponseEntity<List<Papel>> getAll() {
        return new ResponseEntity<List<Papel>>(papelRepository.findAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('Papel.Read.All')")
    @GetMapping("/{papelId}")
    public ResponseEntity<Papel> getPapelByPapelId(@Valid @PathVariable("papelId") Integer papelId) {
        return ResponseEntity.of(papelRepository.findByPapelId(papelId));
    }

    @PreAuthorize("hasAuthority('Papel.Read.All')")
    @GetMapping("/{papelId}/permissao")
    public ResponseEntity<List<Permissao>> getPermissoesByPapelId(@Valid @PathVariable("papelId") Integer papelId) {
        return ResponseEntity.ok(permissaoRepository.findAllByPapelId(papelId));
    }

}
