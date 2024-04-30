package br.net.unicom.backend.controller;

import java.time.LocalTime;
import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("find-by-usuario-id-and-data")
    public ResponseEntity<RegistroJornadaCorrecao> findByUsuarioIdAndData(@Valid @RequestBody RegistroJornadaCorrecaoFindByUsuarioIdAndDataRequest registroJornadaCorrecaoFindByUsuarioIdAndDataRequest ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioPai = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);
        Usuario usuarioFilho = usuarioRepository.findByUsuarioId(registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        if (!usuarioPai.equals(usuarioFilho) && !usuarioService.isUsuarioGreaterThan(usuarioPai, usuarioFilho))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.of(registroJornadaCorrecaoRepository.findByRegistroJornadaCorrecaoKey(new RegistroJornadaCorrecaoKey(usuarioFilho.getUsuarioId(), registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getData())));
    }

    @PatchMapping("/patch-by-usuario-id-and-data")
    @Transactional
    public ResponseEntity<Jornada> patchByUsuarioIdAndData(@Valid @RequestBody RegistroJornadaCorrecaoPatchByUsuarioIdAndDataRequest registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioPai = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);
        Usuario usuarioFilho = usuarioRepository.findByUsuarioId(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        Boolean isGreaterThan = usuarioService.isUsuarioGreaterThan(usuarioPai, usuarioFilho);

        if (!usuarioPai.equals(usuarioFilho) && !isGreaterThan)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        RegistroJornadaCorrecao registroJornadaCorrecao = registroJornadaCorrecaoRepository.findByRegistroJornadaCorrecaoKey(new RegistroJornadaCorrecaoKey(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getUsuarioId(), registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getData())).orElseThrow(NoSuchElementException::new);

        if (registroJornadaCorrecao.getAprovada() && !isGreaterThan)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        registroJornadaCorrecao.setJornadaEntrada(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getJornadaEntrada());
        registroJornadaCorrecao.setJornadaIntervaloInicio(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getJornadaIntervaloInicio());
        registroJornadaCorrecao.setJornadaIntervaloFim(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getJornadaIntervaloFim());
        registroJornadaCorrecao.setJornadaSaida(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getJornadaSaida());
        registroJornadaCorrecao.setContratoId(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getContratoId());
        registroJornadaCorrecao.setJustificativa(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getJustificativa());
        registroJornadaCorrecao.setHorasTrabalhadas(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getHorasTrabalhadas());
        registroJornadaCorrecao.setEntrada(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getEntrada());
        registroJornadaCorrecao.setSaida(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getSaida());
        registroJornadaCorrecao.setHoraExtraPermitida(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getHoraExtraPermitida());
        registroJornadaCorrecao.setObservacao(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getObservacao());
        registroJornadaCorrecao.setAjusteHoraExtra(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getAjusteHoraExtra());

        if (isGreaterThan)
            registroJornadaCorrecao.setAprovada(registroJornadaCorrecaoPatchByUsuarioIdAndDataRequest.getAprovada());

        registroJornadaCorrecaoRepository.saveAndFlush(registroJornadaCorrecao);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create-by-usuario-id-and-data")
    @Transactional
    public ResponseEntity<Jornada> createByUsuarioIdAndData(@Valid @RequestBody RegistroJornadaCorrecaoFindByUsuarioIdAndDataRequest registroJornadaCorrecaoFindByUsuarioIdAndDataRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioPai = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);
        Usuario usuarioFilho = usuarioRepository.findByUsuarioId(registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        if (!usuarioPai.equals(usuarioFilho) && !usuarioService.isUsuarioGreaterThan(usuarioPai, usuarioFilho))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        RegistroJornadaCorrecaoKey registroJornadaCorrecaoKey = new RegistroJornadaCorrecaoKey(registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getUsuarioId(), registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getData());

        if (registroJornadaCorrecaoRepository.existsByRegistroJornadaCorrecaoKey(registroJornadaCorrecaoKey))
            return ResponseEntity.badRequest().build();

        RegistroJornadaCorrecao registroJornadaCorrecao = new RegistroJornadaCorrecao();
        registroJornadaCorrecao.setRegistroJornadaCorrecaoKey(registroJornadaCorrecaoKey);
        registroJornadaCorrecao.setUsuario(usuarioFilho);

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

            if (usuarioFilho.getJornada() != null) {
                registroJornadaCorrecao.setJornadaEntrada(usuarioFilho.getJornada().getEntrada());
                registroJornadaCorrecao.setJornadaIntervaloInicio(usuarioFilho.getJornada().getIntervaloInicio());
                registroJornadaCorrecao.setJornadaIntervaloFim(usuarioFilho.getJornada().getIntervaloFim());
                registroJornadaCorrecao.setJornadaSaida(usuarioFilho.getJornada().getSaida());
            } else {
                registroJornadaCorrecao.setJornadaEntrada(LocalTime.of(8, 0));
                registroJornadaCorrecao.setJornadaIntervaloInicio(LocalTime.of(12, 0));
                registroJornadaCorrecao.setJornadaIntervaloFim(LocalTime.of(14, 0));
                registroJornadaCorrecao.setJornadaSaida(LocalTime.of(17, 0));
            }

            if (usuarioFilho.getContrato() != null) {
                registroJornadaCorrecao.setContratoId(usuarioFilho.getContratoId());
                registroJornadaCorrecao.setContrato(usuarioFilho.getContrato());
            }

            registroJornadaCorrecao.setHoraExtraPermitida(false);
            registroJornadaCorrecao.setHorasTrabalhadas(0);
        }
        
        registroJornadaCorrecao.setJustificativa("");
        registroJornadaCorrecao.setObservacao(null);
        registroJornadaCorrecao.setAjusteHoraExtra(0);
        registroJornadaCorrecao.setAprovada(false);

        registroJornadaCorrecaoRepository.saveAndFlush(registroJornadaCorrecao);
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/delete-by-usuario-id-and-data")
    @Transactional
    public ResponseEntity<Jornada> deleteByUsuarioIdAndData(@Valid @RequestBody RegistroJornadaCorrecaoFindByUsuarioIdAndDataRequest registroJornadaCorrecaoFindByUsuarioIdAndDataRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioPai = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);
        Usuario usuarioFilho = usuarioRepository.findByUsuarioId(registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        Boolean isGreaterThan = usuarioService.isUsuarioGreaterThan(usuarioPai, usuarioFilho);

        if (!usuarioPai.equals(usuarioFilho) && !isGreaterThan)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        RegistroJornadaCorrecao registroJornadaCorrecao = registroJornadaCorrecaoRepository.findByRegistroJornadaCorrecaoKey(new RegistroJornadaCorrecaoKey(registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getUsuarioId(), registroJornadaCorrecaoFindByUsuarioIdAndDataRequest.getData())).orElseThrow(NoSuchElementException::new);

        if (registroJornadaCorrecao.getAprovada() && !isGreaterThan)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        registroJornadaCorrecaoRepository.delete(registroJornadaCorrecao);
        registroJornadaCorrecaoRepository.flush();

        return ResponseEntity.noContent().build();
    }
}
