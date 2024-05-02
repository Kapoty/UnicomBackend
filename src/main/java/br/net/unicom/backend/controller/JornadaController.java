package br.net.unicom.backend.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.net.unicom.backend.model.Jornada;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.payload.request.JornadaFindAllByUsuarioIdRequest;
import br.net.unicom.backend.repository.JornadaRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.UsuarioService;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/jornada",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class JornadaController {

    @Autowired
    JornadaRepository jornadaRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("find-all-by-usuario-id")
    public ResponseEntity<List<Jornada>> findAllByUsuarioId(@Valid @RequestBody JornadaFindAllByUsuarioIdRequest jornadaFindAllByUsuarioIdRequest ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioPai = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);
        Usuario usuarioFilho = usuarioRepository.findByUsuarioId(jornadaFindAllByUsuarioIdRequest.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        if (!usuarioService.isUsuarioGreaterThan(usuarioPai, usuarioFilho))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(jornadaRepository.findAllByUsuarioId(usuarioFilho.getUsuarioId()));
    }

}
