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

import br.net.unicom.backend.model.PontoDeVenda;
import br.net.unicom.backend.payload.request.PontoDeVendaPatchRequest;
import br.net.unicom.backend.payload.request.PontoDeVendaPostRequest;
import br.net.unicom.backend.repository.PontoDeVendaRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/ponto-de-venda",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class PontoDeVendaController {

    @Autowired
    PontoDeVendaRepository pontoDeVendaRepository;

    @Autowired
    ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @GetMapping("/{pontoDeVendaId}")
    public ResponseEntity<PontoDeVenda> getPontoDeVendaByPontoDeVendaId(@Valid @PathVariable("pontoDeVendaId") Integer pontoDeVendaId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.of(pontoDeVendaRepository.findByPontoDeVendaIdAndEmpresaId(pontoDeVendaId, userDetails.getEmpresaId()));
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @PatchMapping("/{pontoDeVendaId}")
    @Transactional
    public ResponseEntity<Void> patchByPontoDeVendaId(@Valid @PathVariable("pontoDeVendaId") Integer pontoDeVendaId, @Valid @RequestBody PontoDeVendaPatchRequest pontoDeVendaPatchRequest) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        PontoDeVenda pontoDeVenda = pontoDeVendaRepository.findByPontoDeVendaIdAndEmpresaId(pontoDeVendaId, userDetails.getEmpresaId()).get();

        modelMapper.map(pontoDeVendaPatchRequest, pontoDeVenda);

        pontoDeVendaRepository.saveAndFlush(pontoDeVenda);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @PostMapping("/")
    @Transactional
    public ResponseEntity<PontoDeVenda> postPontoDeVenda(@Valid @RequestBody PontoDeVendaPostRequest pontoDeVendaPostRequest) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        PontoDeVenda pontoDeVenda = modelMapper.map(pontoDeVendaPostRequest, PontoDeVenda.class);

        pontoDeVenda.setEmpresaId(userDetails.getEmpresaId());
        
        pontoDeVendaRepository.saveAndFlush(pontoDeVenda);

        return ResponseEntity.status(HttpStatus.CREATED).body(pontoDeVenda);
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @DeleteMapping("/{pontoDeVendaId}")
    @Transactional
    public ResponseEntity<Void> deletePontoDeVenda(@Valid @PathVariable("pontoDeVendaId") Integer pontoDeVendaId) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        PontoDeVenda pontoDeVenda = pontoDeVendaRepository.findByPontoDeVendaIdAndEmpresaId(pontoDeVendaId, userDetails.getEmpresaId()).get();

        pontoDeVendaRepository.delete(pontoDeVenda);

        return ResponseEntity.noContent().build();
    }

}
