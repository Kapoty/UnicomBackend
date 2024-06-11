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

import br.net.unicom.backend.model.VendaVisao;
import br.net.unicom.backend.payload.request.VendaVisaoPatchRequest;
import br.net.unicom.backend.payload.request.VendaVisaoPostRequest;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.repository.VendaVisaoRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.UsuarioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/venda-visao",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class VendaVisaoController {

    @Autowired
    VendaVisaoRepository vendaVisaoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @GetMapping("/{vendaVisaoId}")
    public ResponseEntity<VendaVisao> findByVendaVisaoId(@Valid @PathVariable("vendaVisaoId") Integer vendaVisaoId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        VendaVisao vendaVisao = vendaVisaoRepository.findByVendaVisaoId(vendaVisaoId).orElseThrow(NoSuchElementException::new);
        
        if (!vendaVisao.getUsuarioId().equals(userDetails.getUsuarioId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(vendaVisao);
    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @PatchMapping("/{vendaVisaoId}")
    @Transactional
    public ResponseEntity<Void> patchVendaVisao(@Valid @PathVariable("vendaVisaoId") Integer vendaVisaoId, @Valid @RequestBody VendaVisaoPatchRequest vendaVisaoPatchRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        VendaVisao vendaVisao = vendaVisaoRepository.findByVendaVisaoId(vendaVisaoId).orElseThrow(NoSuchElementException::new);

        if (!vendaVisao.getUsuarioId().equals(userDetails.getUsuarioId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        modelMapper.map(vendaVisaoPatchRequest, vendaVisao);

        vendaVisaoRepository.saveAndFlush(vendaVisao);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @PostMapping("/")
    @Transactional
    public ResponseEntity<VendaVisao> postVendaVisao(@Valid @RequestBody VendaVisaoPostRequest vendaVisaoPostRequest) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        VendaVisao vendaVisao = new VendaVisao();
        vendaVisao.setUsuarioId(userDetails.getUsuarioId());
        vendaVisao.setNome(vendaVisaoPostRequest.getNome());
        vendaVisao.setState(vendaVisaoPostRequest.getState());
        vendaVisao.setAtual(false);

        vendaVisaoRepository.save(vendaVisao);

        vendaVisaoRepository.setUsuarioVendaVisualAtual(userDetails.getUsuarioId(), vendaVisao.getVendaVisaoId());

        return ResponseEntity.ok(vendaVisao);

    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @PostMapping("/{vendaVisaoId}/atual")
    @Transactional
    public ResponseEntity<Void> setVendaVisualAtual(@Valid @PathVariable("vendaVisaoId") Integer vendaVisaoId) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        vendaVisaoRepository.setUsuarioVendaVisualAtual(userDetails.getUsuarioId(), vendaVisaoId);

        return ResponseEntity.noContent().build();

    }


    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @DeleteMapping("/{vendaVisaoId}")
    @Transactional
    public ResponseEntity<Void> deleteVendaVisao(@Valid @PathVariable("vendaVisaoId") Integer vendaVisaoId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        VendaVisao vendaVisao = vendaVisaoRepository.findByVendaVisaoId(vendaVisaoId).orElseThrow(NoSuchElementException::new);

        if (!vendaVisao.getUsuarioId().equals(userDetails.getUsuarioId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        vendaVisaoRepository.delete(vendaVisao);

        return ResponseEntity.noContent().build();
    }
    

}
