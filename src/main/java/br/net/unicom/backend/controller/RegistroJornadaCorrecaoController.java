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

import br.net.unicom.backend.model.Jornada;
import br.net.unicom.backend.model.RegistroJornada;
import br.net.unicom.backend.model.RegistroJornadaCorrecao;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.key.RegistroJornadaCorrecaoKey;
import br.net.unicom.backend.payload.request.RegistroJornadaCorrecaoFindByUsuarioIdAndDataRequest;
import br.net.unicom.backend.payload.request.RegistroJornadaCorrecaoPatchByUsuarioIdAndDataRequest;
import br.net.unicom.backend.repository.ContratoRepository;
import br.net.unicom.backend.repository.RegistroJornadaCorrecaoRepository;
import br.net.unicom.backend.repository.RegistroJornadaRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.RegistroJornadaService;
import br.net.unicom.backend.service.UsuarioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;



@RestController
@Validated
@RequestMapping(
    value = "/registro-jornada-correcao",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class RegistroJornadaCorrecaoController {

    @Autowired
    RegistroJornadaCorrecaoRepository registroJornadaCorrecaoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ContratoRepository contratoRepository;

    @Autowired
    RegistroJornadaRepository registroJornadaRepository;

    @Autowired
    RegistroJornadaService registroJornadaService;

    @Autowired
    ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('Equipe.Read.All')")
    @PostMapping("find-by-usuario-id-and-data")
    public ResponseEntity<RegistroJornadaCorrecao> findByUsuarioIdAndData(@Valid @RequestBody RegistroJornadaCorrecaoFindByUsuarioIdAndDataRequest registroJornadaCorrecaoFindByUsuarioIdAndDataRequest ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        if (!usuarioService.isUsuarioSupervisorOf(userDetails.getId(), usuario))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.of(registroJornadaCorrecaoRepository.findByRegistroJornadaCorrecaoKey(new RegistroJornadaCorrecaoKey(usuario.getUsuarioId(), registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getData())));
    }


    @PreAuthorize("hasAuthority('Equipe.Read.All')")
    @PatchMapping("/patch-by-usuario-id-and-data")
    @Transactional
    public ResponseEntity<Jornada> patchByUsuarioIdAndData(@Valid @RequestBody RegistroJornadaCorrecaoPatchByUsuarioIdAndDataRequest registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        if (!usuarioService.isUsuarioSupervisorOf(userDetails.getId(), usuario))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        RegistroJornadaCorrecao registroJornadaCorrecao = registroJornadaCorrecaoRepository.findByRegistroJornadaCorrecaoKey(new RegistroJornadaCorrecaoKey(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getUsuarioId(), registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getData())).orElseThrow(NoSuchElementException::new);

        modelMapper.map(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest, registroJornadaCorrecao);
        registroJornadaCorrecaoRepository.saveAndFlush(registroJornadaCorrecao);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Equipe.Read.All')")
    @PostMapping("/create-by-usuario-id-and-data")
    @Transactional
    public ResponseEntity<Jornada> createByUsuarioIdAndData(@Valid @RequestBody RegistroJornadaCorrecaoFindByUsuarioIdAndDataRequest registroJornadaCorrecaoFindByUsuarioIdAndDataRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        if (!usuarioService.isUsuarioSupervisorOf(userDetails.getId(), usuario))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        RegistroJornadaCorrecao registroJornadaCorrecao = new RegistroJornadaCorrecao();
        registroJornadaCorrecao.setRegistroJornadaCorrecaoKey(new RegistroJornadaCorrecaoKey(registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getUsuarioId(), registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getData()));
        registroJornadaCorrecao.setUsuario(usuario);

        RegistroJornada registroJornada = registroJornadaRepository.findByUsuarioIdAndData(registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getUsuarioId(), registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getData()).orElse(null);

        if (registroJornada != null) {
            registroJornadaCorrecao.setContratoId(registroJornada.getContratoId());
            registroJornadaCorrecao.setContrato(registroJornada.getContrato());
            registroJornadaCorrecao.setJornadaEntrada(registroJornada.getJornadaEntrada());
            registroJornadaCorrecao.setJornadaIntervaloInicio(registroJornada.getJornadaIntervaloInicio());
            registroJornadaCorrecao.setJornadaIntervaloFim(registroJornada.getJornadaIntervaloFim());
            registroJornadaCorrecao.setJornadaSaida(registroJornada.getJornadaSaida());
            registroJornadaCorrecao.setEntrada(registroJornadaService.calculateEntrada(registroJornada).orElse(null));
            registroJornadaCorrecao.setSaida(registroJornadaService.calculateSaida(registroJornada).orElse(null));
            registroJornadaCorrecao.setHorasTrabalhadas(registroJornadaService.calculateHorasTrabalhadas(registroJornada));
            registroJornadaCorrecao.setHoraExtraPermitida(registroJornada.getHoraExtraPermitida());
        } else {
            registroJornadaCorrecao.setJornadaEntrada(LocalTime.of(8, 0));
            registroJornadaCorrecao.setJornadaIntervaloInicio(LocalTime.of(12, 0));
            registroJornadaCorrecao.setJornadaIntervaloFim(LocalTime.of(14, 0));
            registroJornadaCorrecao.setJornadaSaida(LocalTime.of(17, 0));
            registroJornadaCorrecao.setHoraExtraPermitida(false);
            registroJornadaCorrecao.setHorasTrabalhadas(0);
        }
        
        registroJornadaCorrecao.setJustificativa("");
        registroJornadaCorrecao.setObservacao(null);
        registroJornadaCorrecao.setAjusteHoraExtra(0);

        registroJornadaCorrecaoRepository.saveAndFlush(registroJornadaCorrecao);
        
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Equipe.Read.All')")
    @PostMapping("/delete-by-usuario-id-and-data")
    @Transactional
    public ResponseEntity<Jornada> deleteByUsuarioIdAndData(@Valid @RequestBody RegistroJornadaCorrecaoFindByUsuarioIdAndDataRequest registroJornadaCorrecaoFindByUsuarioIdAndDataRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        if (!usuarioService.isUsuarioSupervisorOf(userDetails.getId(), usuario))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        RegistroJornadaCorrecao registroJornadaCorrecao = registroJornadaCorrecaoRepository.findByRegistroJornadaCorrecaoKey(new RegistroJornadaCorrecaoKey(registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getUsuarioId(), registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getData())).orElseThrow(NoSuchElementException::new);

        registroJornadaCorrecaoRepository.delete(registroJornadaCorrecao);
        registroJornadaCorrecaoRepository.flush();

        return ResponseEntity.noContent().build();
    }
}
