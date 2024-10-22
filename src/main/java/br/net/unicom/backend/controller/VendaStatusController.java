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

import br.net.unicom.backend.model.VendaStatus;
import br.net.unicom.backend.payload.request.VendaStatusPatchRequest;
import br.net.unicom.backend.payload.request.VendaStatusPostRequest;
import br.net.unicom.backend.repository.VendaStatusRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/venda-status",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class VendaStatusController {

    @Autowired
    VendaStatusRepository vendaStatusRepository;

    @Autowired
    ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @GetMapping("/{vendaStatusId}")
    public ResponseEntity<VendaStatus> getVendaStatusByVendaStatusId(@Valid @PathVariable("vendaStatusId") Integer vendaStatusId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.of(vendaStatusRepository.findByVendaStatusIdAndEmpresaId(vendaStatusId, userDetails.getEmpresaId()));
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @PatchMapping("/{vendaStatusId}")
    @Transactional
    public ResponseEntity<Void> patchByVendaStatusId(@Valid @PathVariable("vendaStatusId") Integer vendaStatusId, @Valid @RequestBody VendaStatusPatchRequest vendaStatusPatchRequest) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        VendaStatus vendaStatus = vendaStatusRepository.findByVendaStatusIdAndEmpresaId(vendaStatusId, userDetails.getEmpresaId()).get();

        modelMapper.map(vendaStatusPatchRequest, vendaStatus);

        vendaStatusRepository.saveAndFlush(vendaStatus);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @PostMapping("/")
    @Transactional
    public ResponseEntity<VendaStatus> postVendaStatus(@Valid @RequestBody VendaStatusPostRequest vendaStatusPostRequest) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        VendaStatus vendaStatus = modelMapper.map(vendaStatusPostRequest, VendaStatus.class);

        vendaStatus.setEmpresaId(userDetails.getEmpresaId());
        
        vendaStatusRepository.saveAndFlush(vendaStatus);

        return ResponseEntity.status(HttpStatus.CREATED).body(vendaStatus);
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @DeleteMapping("/{vendaStatusId}")
    @Transactional
    public ResponseEntity<Void> deleteVendaStatus(@Valid @PathVariable("vendaStatusId") Integer vendaStatusId) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        VendaStatus vendaStatus = vendaStatusRepository.findByVendaStatusIdAndEmpresaId(vendaStatusId, userDetails.getEmpresaId()).get();

        vendaStatusRepository.delete(vendaStatus);

        return ResponseEntity.noContent().build();
    }

}
