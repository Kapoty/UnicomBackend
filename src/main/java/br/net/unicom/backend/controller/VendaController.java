package br.net.unicom.backend.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.net.unicom.backend.model.Venda;
import br.net.unicom.backend.payload.request.VendaPatchRequest;
import br.net.unicom.backend.payload.request.VendaPostRequest;
import br.net.unicom.backend.repository.VendaRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/venda",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class VendaController {

    @Autowired
    VendaRepository vendaRepository;;

    @Autowired
    ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('Venda.Read.All')")
    @GetMapping("")
    public ResponseEntity<List<Venda>> getAll() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return new ResponseEntity<List<Venda>>(vendaRepository.findAllByEmpresaId(userDetails.getEmpresaId()), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('Venda.Read.All')")
    @GetMapping("/{vendaId}")
    public ResponseEntity<Venda> getVendaByVendaId(@Valid @PathVariable("vendaId") Integer vendaId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.of(vendaRepository.findByVendaIdAndEmpresaId(vendaId, userDetails.getEmpresaId()));
    }

    @PreAuthorize("hasAuthority('Venda.Write.All')")
    @PatchMapping("/{vendaId}")
    @Transactional
    public ResponseEntity<Void> patchByVendaId(@Valid @PathVariable("vendaId") Integer vendaId, @Valid @RequestBody VendaPatchRequest vendaPatchRequest) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Venda venda = vendaRepository.findByVendaIdAndEmpresaId(vendaId, userDetails.getEmpresaId()).orElseThrow(NoSuchElementException::new);

        modelMapper.map(vendaPatchRequest, venda);

        vendaRepository.saveAndFlush(venda);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Venda.Write.All')")
    @PostMapping("/")
    @Transactional
    public ResponseEntity<Venda> postVenda(@Valid @RequestBody VendaPostRequest vendaPostRequest) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Venda venda = modelMapper.map(vendaPostRequest, Venda.class);

        venda.setEmpresaId(userDetails.getEmpresaId());
        
        vendaRepository.saveAndFlush(venda);

        return ResponseEntity.status(HttpStatus.CREATED).body(venda);
    }
}
