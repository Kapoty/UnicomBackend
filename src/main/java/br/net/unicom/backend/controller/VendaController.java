package br.net.unicom.backend.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.net.unicom.backend.model.Venda;
import br.net.unicom.backend.repository.VendaRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;



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
}
