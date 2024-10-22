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

import br.net.unicom.backend.model.Banco;
import br.net.unicom.backend.payload.request.BancoPatchRequest;
import br.net.unicom.backend.payload.request.BancoPostRequest;
import br.net.unicom.backend.repository.BancoRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/banco",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class BancoController {

    @Autowired
    BancoRepository bancoRepository;

    @Autowired
    ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @GetMapping("/{bancoId}")
    public ResponseEntity<Banco> getBancoByBancoId(@Valid @PathVariable("bancoId") Integer bancoId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.of(bancoRepository.findByBancoIdAndEmpresaId(bancoId, userDetails.getEmpresaId()));
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @PatchMapping("/{bancoId}")
    @Transactional
    public ResponseEntity<Void> patchByBancoId(@Valid @PathVariable("bancoId") Integer bancoId, @Valid @RequestBody BancoPatchRequest bancoPatchRequest) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Banco banco = bancoRepository.findByBancoIdAndEmpresaId(bancoId, userDetails.getEmpresaId()).get();

        modelMapper.map(bancoPatchRequest, banco);

        bancoRepository.saveAndFlush(banco);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @PostMapping("/")
    @Transactional
    public ResponseEntity<Banco> postBanco(@Valid @RequestBody BancoPostRequest bancoPostRequest) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Banco banco = modelMapper.map(bancoPostRequest, Banco.class);

        banco.setEmpresaId(userDetails.getEmpresaId());
        
        bancoRepository.saveAndFlush(banco);

        return ResponseEntity.status(HttpStatus.CREATED).body(banco);
    }

    @PreAuthorize("hasAuthority('ALTERAR_EMPRESA')")
    @DeleteMapping("/{bancoId}")
    @Transactional
    public ResponseEntity<Void> deleteBanco(@Valid @PathVariable("bancoId") Integer bancoId) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Banco banco = bancoRepository.findByBancoIdAndEmpresaId(bancoId, userDetails.getEmpresaId()).get();

        bancoRepository.delete(banco);

        return ResponseEntity.noContent().build();
    }

}
