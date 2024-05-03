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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.net.unicom.backend.model.Jornada;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.payload.request.JornadaFindAllByUsuarioIdRequest;
import br.net.unicom.backend.payload.request.JornadaPatchRequest;
import br.net.unicom.backend.payload.request.JornadaPostRequest;
import br.net.unicom.backend.repository.JornadaRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.UsuarioService;
import jakarta.transaction.Transactional;
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

    @GetMapping("/{jornadaId}")
    public ResponseEntity<Jornada> findByJornadaId(@Valid @PathVariable("jornadaId") Integer jornadaId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Jornada jornada = jornadaRepository.findByJornadaId(jornadaId).orElseThrow(NoSuchElementException::new);

        if (jornada.getUsuarioId() != null) {
            Usuario usuarioPai = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);
            Usuario usuarioFilho = usuarioRepository.findByUsuarioId(jornada.getUsuarioId()).orElseThrow(NoSuchElementException::new);
            
            if (!usuarioService.isUsuarioGreaterThan(usuarioPai, usuarioFilho))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else if (!userDetails.hasAuthority("VER_TODAS_EQUIPES"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(jornada);
    }

    @PatchMapping("/{jornadaId}")
    @Transactional
    public ResponseEntity<Void> patchJornada(@Valid @PathVariable("jornadaId") Integer jornadaId, @Valid @RequestBody JornadaPatchRequest jornadaPatchRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Jornada jornada = jornadaRepository.findByJornadaId(jornadaId).orElseThrow(NoSuchElementException::new);

        if (jornada.getUsuarioId() != null) {
            Usuario usuarioPai = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);
            Usuario usuarioFilho = usuarioRepository.findByUsuarioId(jornada.getUsuarioId()).orElseThrow(NoSuchElementException::new);
            
            if (!usuarioService.isUsuarioGreaterThan(usuarioPai, usuarioFilho))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else if (!userDetails.hasAuthority("VER_TODAS_EQUIPES"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        modelMapper.map(jornadaPatchRequest, jornada);

        jornadaRepository.saveAndFlush(jornada);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/")
    @Transactional
    public ResponseEntity<Jornada> postJornada(@Valid @RequestBody JornadaPostRequest jornadaPostRequest) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (jornadaPostRequest.getUsuarioId() == null && !userDetails.hasAuthority("VER_TODAS_EQUIPES"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (jornadaPostRequest.getUsuarioId() != null) {
            Usuario usuarioPai = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);
            Usuario usuarioFilho = usuarioRepository.findByUsuarioId(jornadaPostRequest.getUsuarioId()).orElseThrow(NoSuchElementException::new);
            
            if (!usuarioService.isUsuarioGreaterThan(usuarioPai, usuarioFilho))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Jornada jornada = new Jornada();
        jornada.setUsuarioId(jornadaPostRequest.getUsuarioId());
        jornada.setEmpresaId(userDetails.getEmpresaId());
        jornada.setNome("Sem Nome");
        jornada.setPrioridade(jornadaPostRequest.getUsuarioId() == null ? 10 : 1);

        jornadaRepository.save(jornada);

        return ResponseEntity.ok(jornada);

    }

    @DeleteMapping("/{jornadaId}")
    @Transactional
    public ResponseEntity<Void> patchJornada(@Valid @PathVariable("jornadaId") Integer jornadaId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Jornada jornada = jornadaRepository.findByJornadaId(jornadaId).orElseThrow(NoSuchElementException::new);

        if (jornada.getUsuarioId() != null) {
            Usuario usuarioPai = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);
            Usuario usuarioFilho = usuarioRepository.findByUsuarioId(jornada.getUsuarioId()).orElseThrow(NoSuchElementException::new);
            
            if (!usuarioService.isUsuarioGreaterThan(usuarioPai, usuarioFilho))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else if (!userDetails.hasAuthority("VER_TODAS_EQUIPES"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        jornadaRepository.delete(jornada);

        return ResponseEntity.noContent().build();
    }
    

}
