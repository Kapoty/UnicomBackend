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
import br.net.unicom.backend.model.Grupo;
import br.net.unicom.backend.repository.GrupoRepository;
import br.net.unicom.backend.service.GrupoService;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/grupos",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class GrupoController {

    @Autowired
    GrupoRepository grupoRepository;

    @Autowired
    GrupoService grupoService;

    @GetMapping("")
    public ResponseEntity<List<Grupo>> getAll() {
        return new ResponseEntity<List<Grupo>>(grupoRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{grupoId}")
    public ResponseEntity<Grupo> getGrupoByGrupoId(@Valid @PathVariable("grupoId") Integer grupoId) {
        return ResponseEntity.of(grupoRepository.findByGrupoId(grupoId));
    }

    @GetMapping("/{grupoId}/empresas")
    public ResponseEntity<List<Empresa>> getMethodName(@Valid @PathVariable("grupoId") Integer grupoId) {
        return ResponseEntity.of(grupoService.getEmpresasByGrupoId(grupoId));
    }

}
