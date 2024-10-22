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

import br.net.unicom.backend.model.Origem;
import br.net.unicom.backend.payload.request.OrigemPatchRequest;
import br.net.unicom.backend.payload.request.OrigemPostRequest;
import br.net.unicom.backend.repository.OrigemRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/origem",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class OrigemController {

    @Autowired
    OrigemRepository origemRepository;

    @Autowired
    ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @GetMapping("/{origemId}")
    public ResponseEntity<Origem> getOrigemByOrigemId(@Valid @PathVariable("origemId") Integer origemId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.of(origemRepository.findByOrigemIdAndEmpresaId(origemId, userDetails.getEmpresaId()));
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @PatchMapping("/{origemId}")
    @Transactional
    public ResponseEntity<Void> patchByOrigemId(@Valid @PathVariable("origemId") Integer origemId, @Valid @RequestBody OrigemPatchRequest origemPatchRequest) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Origem origem = origemRepository.findByOrigemIdAndEmpresaId(origemId, userDetails.getEmpresaId()).get();

        modelMapper.map(origemPatchRequest, origem);

        origemRepository.saveAndFlush(origem);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @PostMapping("/")
    @Transactional
    public ResponseEntity<Origem> postOrigem(@Valid @RequestBody OrigemPostRequest origemPostRequest) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Origem origem = modelMapper.map(origemPostRequest, Origem.class);

        origem.setEmpresaId(userDetails.getEmpresaId());
        
        origemRepository.saveAndFlush(origem);

        return ResponseEntity.status(HttpStatus.CREATED).body(origem);
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @DeleteMapping("/{origemId}")
    @Transactional
    public ResponseEntity<Void> deleteOrigem(@Valid @PathVariable("origemId") Integer origemId) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Origem origem = origemRepository.findByOrigemIdAndEmpresaId(origemId, userDetails.getEmpresaId()).get();

        origemRepository.delete(origem);

        return ResponseEntity.noContent().build();
    }

}
