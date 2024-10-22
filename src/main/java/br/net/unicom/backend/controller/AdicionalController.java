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

import br.net.unicom.backend.model.Adicional;
import br.net.unicom.backend.payload.request.AdicionalPatchRequest;
import br.net.unicom.backend.payload.request.AdicionalPostRequest;
import br.net.unicom.backend.repository.AdicionalRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/adicional",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class AdicionalController {

    @Autowired
    AdicionalRepository adicionalRepository;

    @Autowired
    ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @GetMapping("/{adicionalId}")
    public ResponseEntity<Adicional> getAdicionalByAdicionalId(@Valid @PathVariable("adicionalId") Integer adicionalId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.of(adicionalRepository.findByAdicionalIdAndEmpresaId(adicionalId, userDetails.getEmpresaId()));
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @PatchMapping("/{adicionalId}")
    @Transactional
    public ResponseEntity<Void> patchByAdicionalId(@Valid @PathVariable("adicionalId") Integer adicionalId, @Valid @RequestBody AdicionalPatchRequest adicionalPatchRequest) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Adicional adicional = adicionalRepository.findByAdicionalIdAndEmpresaId(adicionalId, userDetails.getEmpresaId()).get();

        modelMapper.map(adicionalPatchRequest, adicional);

        adicionalRepository.saveAndFlush(adicional);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @PostMapping("/")
    @Transactional
    public ResponseEntity<Adicional> postAdicional(@Valid @RequestBody AdicionalPostRequest adicionalPostRequest) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Adicional adicional = modelMapper.map(adicionalPostRequest, Adicional.class);

        adicional.setEmpresaId(userDetails.getEmpresaId());
        
        adicionalRepository.saveAndFlush(adicional);

        return ResponseEntity.status(HttpStatus.CREATED).body(adicional);
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @DeleteMapping("/{adicionalId}")
    @Transactional
    public ResponseEntity<Void> deleteAdicional(@Valid @PathVariable("adicionalId") Integer adicionalId) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Adicional adicional = adicionalRepository.findByAdicionalIdAndEmpresaId(adicionalId, userDetails.getEmpresaId()).get();

        adicionalRepository.delete(adicional);

        return ResponseEntity.noContent().build();
    }

}
