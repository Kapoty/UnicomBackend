package br.net.unicom.backend.controller;

import java.time.LocalTime;
import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.net.unicom.backend.model.Contrato;
import br.net.unicom.backend.model.Jornada;
import br.net.unicom.backend.model.JornadaExcecao;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.payload.request.CreateJornadaExcecaoByUsuarioIdAndDataRequest;
import br.net.unicom.backend.payload.request.JornadaExcecaoFindByUsuarioIdAndDataRequest;
import br.net.unicom.backend.payload.request.PatchJornadaExcecaoByUsuarioIdAndDataRequest;
import br.net.unicom.backend.repository.JornadaExcecaoRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.UsuarioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/jornada-excecao",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class JornadaExcecaoController {

    @Autowired
    JornadaExcecaoRepository jornadaExcecaoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('VER_MODULO_MINHA_EQUIPE')")
    @PostMapping("find-by-usuario-id-and-data")
    public ResponseEntity<JornadaExcecao> findByUsuarioIdAndData(@Valid @RequestBody JornadaExcecaoFindByUsuarioIdAndDataRequest jornadaExcecaoFindByUsuarioIdAndDataRequest ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioPai = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);
        Usuario usuarioFilho = usuarioRepository.findByUsuarioId(jornadaExcecaoFindByUsuarioIdAndDataRequest.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        if (!usuarioService.isUsuarioGreaterThan(usuarioPai, usuarioFilho))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.of(jornadaExcecaoRepository.findByUsuarioIdAndData(usuarioFilho.getUsuarioId(), jornadaExcecaoFindByUsuarioIdAndDataRequest.getData()));
    }

    @PreAuthorize("hasAuthority('VER_MODULO_MINHA_EQUIPE')")
    @PatchMapping("/patch-by-usuario-id-and-data")
    @Transactional
    public ResponseEntity<Jornada> patchByUsuarioIdAndData(@Valid @RequestBody PatchJornadaExcecaoByUsuarioIdAndDataRequest patchJornadaExcecaoByUsuarioIdAndDataRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioPai = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);
        Usuario usuarioFilho = usuarioRepository.findByUsuarioId(patchJornadaExcecaoByUsuarioIdAndDataRequest.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        if (!usuarioService.isUsuarioGreaterThan(usuarioPai, usuarioFilho))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        JornadaExcecao jornadaExcecao = jornadaExcecaoRepository.findByUsuarioIdAndData(patchJornadaExcecaoByUsuarioIdAndDataRequest.getUsuarioId(), patchJornadaExcecaoByUsuarioIdAndDataRequest.getData()).orElseThrow(NoSuchElementException::new);

        modelMapper.map(patchJornadaExcecaoByUsuarioIdAndDataRequest, jornadaExcecao);
        jornadaExcecaoRepository.saveAndFlush(jornadaExcecao);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('VER_MODULO_MINHA_EQUIPE')")
    @PostMapping("/create-by-usuario-id-and-data")
    @Transactional
    public ResponseEntity<Jornada> createByUsuarioIdAndData(@Valid @RequestBody CreateJornadaExcecaoByUsuarioIdAndDataRequest createJornadaExcecaoByUsuarioIdAndDataRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioPai = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);
        Usuario usuarioFilho = usuarioRepository.findByUsuarioId(createJornadaExcecaoByUsuarioIdAndDataRequest.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        if (!usuarioService.isUsuarioGreaterThan(usuarioPai, usuarioFilho))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        JornadaExcecao jornadaExcecao = new JornadaExcecao();
        jornadaExcecao.setUsuarioId(createJornadaExcecaoByUsuarioIdAndDataRequest.getUsuarioId());
        jornadaExcecao.setData(createJornadaExcecaoByUsuarioIdAndDataRequest.getData());

        if (usuarioFilho.getJornada() != null) {
            jornadaExcecao.setEntrada(usuarioFilho.getJornada().getEntrada());
            jornadaExcecao.setIntervaloInicio(usuarioFilho.getJornada().getIntervaloInicio());
            jornadaExcecao.setIntervaloFim(usuarioFilho.getJornada().getIntervaloFim());
            jornadaExcecao.setSaida(usuarioFilho.getJornada().getSaida());
        } else {
            jornadaExcecao.setEntrada(LocalTime.of(8, 0));
            jornadaExcecao.setIntervaloInicio(LocalTime.of(12, 0));
            jornadaExcecao.setIntervaloFim(LocalTime.of(14, 0));
            jornadaExcecao.setSaida(LocalTime.of(17, 0));
        }
        if (usuarioFilho.getContrato() != null) {
            
            Contrato contrato = usuarioFilho.getContrato();
            Boolean registraPonto = true;
            switch (createJornadaExcecaoByUsuarioIdAndDataRequest.getData().getDayOfWeek().getValue()) {
                case 1:
                    if (!contrato.getRPSegunda())
                        registraPonto = false;
                    break;
                case 2:
                    if (!contrato.getRPTerca())
                        registraPonto = false;
                    break;
                case 3:
                    if (!contrato.getRPQuarta())
                        registraPonto = false;
                    break;
                case 4:
                    if (!contrato.getRPQuinta())
                        registraPonto = false;
                    break;
                case 5:
                    if (!contrato.getRPSexta())
                        registraPonto = false;
                    break;
                case 6:
                    if (!contrato.getRPSabado())
                        registraPonto = false;
                    break;
                case 7:
                    if (!contrato.getRPDomingo())
                        registraPonto = false;
                    break;
            }

            jornadaExcecao.setRegistraPonto(registraPonto);
        } else {
            jornadaExcecao.setRegistraPonto(true);
        }
        
        jornadaExcecaoRepository.saveAndFlush(jornadaExcecao);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('VER_MODULO_MINHA_EQUIPE')")
    @PostMapping("/delete-by-usuario-id-and-data")
    @Transactional
    public ResponseEntity<Jornada> deleteByUsuarioIdAndData(@Valid @RequestBody JornadaExcecaoFindByUsuarioIdAndDataRequest jornadaExcecaoFindByUsuarioIdAndDataRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioPai = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);
        Usuario usuarioFilho = usuarioRepository.findByUsuarioId(jornadaExcecaoFindByUsuarioIdAndDataRequest.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        if (!usuarioService.isUsuarioGreaterThan(usuarioPai, usuarioFilho))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        JornadaExcecao jornadaExcecao = jornadaExcecaoRepository.findByUsuarioIdAndData(jornadaExcecaoFindByUsuarioIdAndDataRequest.getUsuarioId(), jornadaExcecaoFindByUsuarioIdAndDataRequest.getData()).orElseThrow(NoSuchElementException::new);

        jornadaExcecaoRepository.delete(jornadaExcecao);
        jornadaExcecaoRepository.flush();

        return ResponseEntity.noContent().build();
    }
}
