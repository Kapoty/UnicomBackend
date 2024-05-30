package br.net.unicom.backend.controller;

import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.net.unicom.backend.model.Equipe;
import br.net.unicom.backend.payload.response.MinhaEquipeResponse;
import br.net.unicom.backend.payload.response.UsuarioPublicResponse;
import br.net.unicom.backend.repository.EquipeRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.UsuarioService;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/minha-equipe",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class MinhaEquipeController {

    @Autowired
    EquipeRepository equipeRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('VER_MODULO_MINHA_EQUIPE')")
    @GetMapping("{equipeId}")
    public ResponseEntity<MinhaEquipeResponse> getMinhaEquipeByEquipeId(@Valid @PathVariable("equipeId") Integer equipeId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Equipe> equipe = equipeRepository.findByEquipeId(equipeId);
        if (equipe.isEmpty())
            return ResponseEntity.notFound().build();
        if (equipe.get().getSupervisorId() != userDetails.getId() && equipe.get().getGerenteId() != userDetails.getId() && !userDetails.hasAuthority("VER_TODAS_EQUIPES"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        MinhaEquipeResponse minhaEquipeResponse = modelMapper.map(equipe.get(), MinhaEquipeResponse.class);
        minhaEquipeResponse.setUsuarioList(
            usuarioRepository.findAllByEquipeId(equipeId)
            .stream()
            .map(usuario -> modelMapper.map(usuario, UsuarioPublicResponse.class))
            .collect(Collectors.toList())
            );
        return ResponseEntity.ok(minhaEquipeResponse);
    }

}
