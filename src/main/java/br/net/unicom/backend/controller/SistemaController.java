package br.net.unicom.backend.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.net.unicom.backend.model.Sistema;
import br.net.unicom.backend.payload.request.SistemaPatchRequest;
import br.net.unicom.backend.payload.request.SistemaPostRequest;
import br.net.unicom.backend.repository.SistemaRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/sistema",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class SistemaController {

    @Autowired
    SistemaRepository sistemaRepository;

    @Autowired
    ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @GetMapping("/{sistemaId}")
    public ResponseEntity<Sistema> getSistemaBySistemaId(@Valid @PathVariable("sistemaId") Integer sistemaId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.of(sistemaRepository.findBySistemaIdAndEmpresaId(sistemaId, userDetails.getEmpresaId()));
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @PatchMapping("/{sistemaId}")
    @Transactional
    public ResponseEntity<Void> patchBySistemaId(@Valid @PathVariable("sistemaId") Integer sistemaId, @Valid @RequestBody SistemaPatchRequest sistemaPatchRequest) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Sistema sistema = sistemaRepository.findBySistemaIdAndEmpresaId(sistemaId, userDetails.getEmpresaId()).get();

        modelMapper.map(sistemaPatchRequest, sistema);

        sistemaRepository.saveAndFlush(sistema);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @PostMapping("/")
    @Transactional
    public ResponseEntity<Sistema> postSistema(@Valid @RequestBody SistemaPostRequest sistemaPostRequest) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Sistema sistema = modelMapper.map(sistemaPostRequest, Sistema.class);

        sistema.setEmpresaId(userDetails.getEmpresaId());
        
        sistemaRepository.saveAndFlush(sistema);

        return ResponseEntity.status(HttpStatus.CREATED).body(sistema);
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @DeleteMapping("/{sistemaId}")
    @Transactional
    public ResponseEntity<Void> deleteSistema(@Valid @PathVariable("sistemaId") Integer sistemaId) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Sistema sistema = sistemaRepository.findBySistemaIdAndEmpresaId(sistemaId, userDetails.getEmpresaId()).get();

        sistemaRepository.delete(sistema);

        return ResponseEntity.noContent().build();
    }

}
