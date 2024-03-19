package br.net.unicom.backend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.net.unicom.backend.model.Contrato;
import br.net.unicom.backend.model.Jornada;
import br.net.unicom.backend.model.RegistroPonto;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.exception.RegistroPontoFullException;
import br.net.unicom.backend.model.exception.RegistroPontoLockedException;
import br.net.unicom.backend.model.exception.RegistroPontoUnauthorizedException;
import br.net.unicom.backend.model.exception.UsuarioNaoRegistraPontoHojeException;
import br.net.unicom.backend.model.exception.UsuarioSemContratoException;
import br.net.unicom.backend.model.exception.UsuarioSemJornadaException;
import br.net.unicom.backend.payload.request.RegistroPontoRegistrarFaceRequest;
import br.net.unicom.backend.payload.request.RegistroPontoRegistrarRequest;
import br.net.unicom.backend.payload.request.RegistroPontoValidateTokenRequest;
import br.net.unicom.backend.payload.response.RegistroPontoLockedSecondsResponse;
import br.net.unicom.backend.payload.response.RegistroPontoTokenResponse;
import br.net.unicom.backend.repository.RegistroPontoRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.security.jwt.PontoJwtUtils;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.DeepFaceService;
import br.net.unicom.backend.service.RegistroPontoService;
import br.net.unicom.backend.service.UsuarioService;
import jakarta.validation.Valid;




@RestController
@Validated
@RequestMapping(
    value = "/registro-ponto",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class RegistroPontoController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    RegistroPontoRepository registroPontoRepository;

    @Autowired
    RegistroPontoService registroPontoService;

    @Autowired
    PontoJwtUtils pontoJwtUtils;

    @Autowired
    DeepFaceService deepFaceService;

    @Autowired
    ObjectMapper objectMapper;

    Logger logger = LoggerFactory.getLogger(RegistroPontoController.class);

    @PreAuthorize("hasAuthority('Ponto.Read.All')")
    @GetMapping("/me/hoje")
    public ResponseEntity<RegistroPonto> getRegistroPontoByMeHoje() throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getId()).get();

        Contrato contrato = Optional.ofNullable(usuario.getContrato()).orElseThrow(UsuarioSemContratoException::new);

        Jornada jornada = Optional.ofNullable(usuario.getJornada()).orElseThrow(UsuarioSemJornadaException::new);

        LocalDate hoje = LocalDate.now();

        Boolean registraPontoHoje = true;
        switch (hoje.getDayOfWeek().getValue()) {
            case 1:
                if (!contrato.getRPSegunda())
                    registraPontoHoje = false;
                break;
            case 2:
                if (!contrato.getRPTerca())
                    registraPontoHoje = false;
                break;
            case 3:
                if (!contrato.getRPQuarta())
                    registraPontoHoje = false;
                break;
            case 4:
                if (!contrato.getRPQuinta())
                    registraPontoHoje = false;
                break;
            case 5:
                if (!contrato.getRPSexta())
                    registraPontoHoje = false;
                break;
            case 6:
                if (!contrato.getRPSabado())
                    registraPontoHoje = false;
                break;
            case 7:
                if (!contrato.getRPDomingo())
                    registraPontoHoje = false;
                break;
        }
        if (!registraPontoHoje)
            throw new UsuarioNaoRegistraPontoHojeException();

        RegistroPonto registroPonto = registroPontoRepository.findByUsuarioIdAndData(usuario.getUsuarioId(), hoje).orElse(null);
        if (registroPonto == null) {
            registroPonto = new RegistroPonto();
            registroPonto.setUsuarioId(usuario.getUsuarioId());
            registroPonto.setContratoNome(contrato.getNome());
            registroPonto.setData(hoje);
            registroPonto.setJornadaEntrada(jornada.getEntrada());
            registroPonto.setJornadaIntervaloInicio(jornada.getIntervaloInicio());
            registroPonto.setJornadaIntervaloFim(jornada.getIntervaloFim());
            registroPonto.setJornadaSaida(jornada.getSaida());
            registroPontoRepository.save(registroPonto);
        }

        return ResponseEntity.ok(registroPonto);
    }

    @PreAuthorize("hasAuthority('Ponto.Read.All')")
    @PostMapping("/me/hoje/registrar")
    public ResponseEntity<Void> registrarPontoByMeHoje(@Valid @RequestBody RegistroPontoRegistrarRequest registroPontoegistroPontoRegistrarRequest) throws RegistroPontoFullException, RegistroPontoLockedException, RegistroPontoUnauthorizedException  {

        if (!pontoJwtUtils.validateJwtToken(registroPontoegistroPontoRegistrarRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        usuarioService.registrarPontoByUsuarioId(userDetails.getId());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/registrar-face")
    public ResponseEntity<Optional<Usuario>> registrarPontoByFace(@Valid @RequestBody RegistroPontoRegistrarFaceRequest registroPontoRegistrarFaceRequest) throws RegistroPontoFullException, RegistroPontoLockedException, RegistroPontoUnauthorizedException  {

        if (!pontoJwtUtils.validateJwtToken(registroPontoRegistrarFaceRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();

        JsonNode results;
        JsonNode result;
        String identity;
        Integer usuarioId;
        Optional<Usuario> usuario = null;

        try {
            String find = deepFaceService.find(registroPontoRegistrarFaceRequest.getImg(), "/uploads/usuario");
            results = objectMapper.readTree(find).get("results");
            result = objectMapper.readTree(results.get(0).asText());
            identity = result.get("identity").get("0").asText();
            logger.info(identity);
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(identity);
            if (m.find()) {
                logger.info("sim");
                usuarioId = Integer.valueOf(m.group(1));
                usuario = usuarioRepository.findByUsuarioId(usuarioId);
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        //usuarioService.registrarPontoByUsuarioId(userDetails.getId());

        return ResponseEntity.ofNullable(usuario);
    }

    @PreAuthorize("hasAuthority('Ponto.Read.All')")
    @GetMapping("/me/hoje/locked-seconds")
    public ResponseEntity<RegistroPontoLockedSecondsResponse> getPontoByMeHojeLockedSeconds() throws RegistroPontoFullException, RegistroPontoLockedException  {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LocalDateTime hoje = LocalDateTime.now();

        RegistroPonto registroPonto = registroPontoRepository.findByUsuarioIdAndData(userDetails.getId(), hoje.toLocalDate()).orElseThrow(NoSuchElementException::new);

        Integer lockedSeconds = registroPontoService.getLockedSeconds(registroPonto);

        return ResponseEntity.ok(new RegistroPontoLockedSecondsResponse(lockedSeconds));
    }

    @PreAuthorize("hasAuthority('Ponto.Write.All')")
    @GetMapping("/generate-token")
    public ResponseEntity<RegistroPontoTokenResponse> generatePontoJwtToken() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(new RegistroPontoTokenResponse(pontoJwtUtils.generateJwtToken(userDetails.getId())));
    }

    @PreAuthorize("hasAuthority('Ponto.Read.All')")
    @PostMapping("/validate-token")
    public ResponseEntity<Void> validatePontoJwtToken(@Valid @RequestBody RegistroPontoValidateTokenRequest registroPontoValidateTokenRequest) {
        if (pontoJwtUtils.validateJwtToken(registroPontoValidateTokenRequest.getToken()))
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.badRequest().build();
    }

}
