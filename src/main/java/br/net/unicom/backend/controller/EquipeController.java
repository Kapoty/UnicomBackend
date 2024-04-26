package br.net.unicom.backend.controller;

import java.util.NoSuchElementException;

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

import br.net.unicom.backend.model.Equipe;
import br.net.unicom.backend.payload.request.EquipePatchRequest;
import br.net.unicom.backend.payload.request.EquipePostRequest;
import br.net.unicom.backend.payload.response.EquipeResponse;
import br.net.unicom.backend.repository.EquipeRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.EquipeService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/equipe",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class EquipeController {

    @Autowired
    EquipeRepository equipeRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EquipeService equipeService;

    @PreAuthorize("hasAuthority('CADASTRAR_EQUIPES')")
    @GetMapping("/{equipeId}")
    public ResponseEntity<Equipe> getEquipeByEquipeId(@Valid @PathVariable("equipeId") Integer equipeId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.of(equipeRepository.findByEquipeIdAndEmpresaId(equipeId, userDetails.getEmpresaId()));
    }

    @PreAuthorize("hasAuthority('CADASTRAR_EQUIPES')")
    @PatchMapping("/{equipeId}")
    @Transactional
    public ResponseEntity<Void> patchByEquipeId(@Valid @PathVariable("equipeId") Integer equipeId, @Valid @RequestBody EquipePatchRequest equipePatchRequest) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Equipe equipe = equipeRepository.findByEquipeIdAndEmpresaId(equipeId, userDetails.getEmpresaId()).orElseThrow(NoSuchElementException::new);

        modelMapper.map(equipePatchRequest, equipe);

        equipeRepository.saveAndFlush(equipe);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('CADASTRAR_EQUIPES')")
    @PostMapping("/")
    @Transactional
    public ResponseEntity<EquipeResponse> postEquipe(@Valid @RequestBody EquipePostRequest equipePostRequest) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Equipe equipe = modelMapper.map(equipePostRequest, Equipe.class);

        equipe.setEmpresaId(userDetails.getEmpresaId());
        
        equipeRepository.saveAndFlush(equipe);

        EquipeResponse equipeResponse = equipeService.equipeToEquipeResponse(equipe);

        return ResponseEntity.status(HttpStatus.CREATED).body(equipeResponse);
    }

    @PreAuthorize("hasAuthority('CADASTRAR_EQUIPES')")
    @DeleteMapping("/{equipeId}")
    @Transactional
    public ResponseEntity<EquipeResponse> deleteEquipe(@Valid @PathVariable("equipeId") Integer equipeId) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Equipe equipe = equipeRepository.findByEquipeIdAndEmpresaId(equipeId, userDetails.getEmpresaId()).orElseThrow(NoSuchElementException::new);

        equipeRepository.delete(equipe);

        return ResponseEntity.noContent().build();
    }

}
