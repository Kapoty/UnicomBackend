package br.net.unicom.backend.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.net.unicom.backend.model.PontoConfiguracao;
import br.net.unicom.backend.model.RegistroJornada;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.exception.JornadaStatusNaoEncontradoException;
import br.net.unicom.backend.model.exception.JornadaStatusNaoPermitidoException;
import br.net.unicom.backend.model.exception.PontoConfiguracaoNaoEncontradoException;
import br.net.unicom.backend.model.exception.RegistroPontoUnauthorizedException;
import br.net.unicom.backend.model.exception.UsuarioNaoRegistraPontoHojeException;
import br.net.unicom.backend.model.exception.UsuarioSemContratoException;
import br.net.unicom.backend.model.exception.UsuarioSemJornadaException;
import br.net.unicom.backend.model.key.RegistroJornadaCorrecaoKey;
import br.net.unicom.backend.payload.request.RegistroJornadaAlterarStatusRequest;
import br.net.unicom.backend.payload.request.RegistroJornadaLogarRequest;
import br.net.unicom.backend.payload.request.RegistroJornadaReportByUsuarioIdRequest;
import br.net.unicom.backend.payload.request.RegistroPontoValidateTokenRequest;
import br.net.unicom.backend.payload.response.JornadaStatusGroupedResponse;
import br.net.unicom.backend.payload.response.RegistroJornadaReportDayResponse;
import br.net.unicom.backend.payload.response.RegistroJornadaReportResponse;
import br.net.unicom.backend.payload.response.RegistroJornadaReportUsuarioResponse;
import br.net.unicom.backend.payload.response.RegistroJornadaResponse;
import br.net.unicom.backend.payload.response.RegistroPontoTokenResponse;
import br.net.unicom.backend.repository.JornadaExcecaoRepository;
import br.net.unicom.backend.repository.JornadaStatusRepository;
import br.net.unicom.backend.repository.PontoConfiguracaoRepository;
import br.net.unicom.backend.repository.RegistroJornadaCorrecaoRepository;
import br.net.unicom.backend.repository.RegistroJornadaRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.security.jwt.PontoJwtUtils;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.RegistroJornadaService;
import br.net.unicom.backend.service.UsuarioService;
import jakarta.validation.Valid;




@RestController
@Validated
@RequestMapping(
    value = "/registro-jornada",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class RegistroJornadaController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    RegistroJornadaRepository registroJornadaRepository;

    @Autowired
    JornadaExcecaoRepository jornadaExcecaoRepository;

    @Autowired
    RegistroJornadaService registroJornadaService;

    @Autowired
    JornadaStatusRepository jornadaStatusRepository;

    @Autowired
    PontoConfiguracaoRepository pontoConfiguracaoRepository;

    @Autowired
    RegistroJornadaCorrecaoRepository registroJornadaCorrecaoRepository;

    @Autowired
    PontoJwtUtils pontoJwtUtils;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ObjectMapper objectMapper;

    Logger logger = LoggerFactory.getLogger(RegistroPontoController.class);

    @PreAuthorize("hasAuthority('REGISTRAR_JORNADA')")
    @GetMapping("/{usuarioId}/hoje")
    public ResponseEntity<RegistroJornadaResponse> getRegistroJornadaByMeHoje(@PathVariable("usuarioId") String usuarioId, @RequestParam(defaultValue = "false") Boolean completo) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario  = usuarioRepository.findByUsuarioId(usuarioService.parseUsuarioIdString(userDetails, usuarioId)).orElseThrow(NoSuchElementException::new);

        if (userDetails.getId() != usuario.getUsuarioId() && !usuarioService.isUsuarioSupervisorOf(userDetails.getId(), usuario) && !userDetails.hasAuthority("VER_TODAS_EQUIPES"))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        PontoConfiguracao pontoConfiguracao = pontoConfiguracaoRepository.findByEmpresaId(usuario.getEmpresaId()).orElseThrow(PontoConfiguracaoNaoEncontradoException::new);

        RegistroJornada registroJornada = registroJornadaService.getRegistroJornadaByUsuarioIdHoje(usuario.getUsuarioId());

        RegistroJornadaResponse registroJornadaResponse = modelMapper.map(registroJornada, RegistroJornadaResponse.class);

        registroJornadaResponse.setStatusAtual(registroJornadaService.getRegistroJornadaStatusAtual(registroJornada));

        registroJornadaResponse.setSecondsToAusente(registroJornadaService.getSecondsToAusente(registroJornada));

        registroJornadaResponse.setCompleto(completo);

        if (completo) {
            registroJornadaResponse.setCanUsuarioLogar(Optional.of(registroJornadaService.canUsuarioLogar(registroJornada)));

            registroJornadaResponse.setCanSupervisorLogar(Optional.of(registroJornadaService.canSupervisorLogar(registroJornada)));

            registroJornadaResponse.setCanUsuarioIniciarHoraExtra(Optional.of(registroJornadaService.canUsuarioIniciarHoraExtra(registroJornada)));

            registroJornadaResponse.setCanUsuarioDeslogar(Optional.of(registroJornadaService.canUsuarioDeslogar(registroJornada)));

            registroJornadaResponse.setStatusGroupedList(Optional.of(registroJornadaService.getStatusGroupedList(registroJornada)));

            registroJornadaResponse.setStatusOptionList(Optional.of(registroJornadaService.getStatusOptionList(registroJornada)));

            registroJornadaResponse.setStatusRegularId(Optional.of(pontoConfiguracao.getStatusRegularId()));

            registroJornadaResponse.setStatusHoraExtraId(Optional.of(pontoConfiguracao.getStatusHoraExtraId()));

            registroJornadaResponse.setStatusAusenteId(Optional.of(pontoConfiguracao.getStatusAusenteId()));
        }

        return ResponseEntity.ok(registroJornadaResponse);
    }

    @PreAuthorize("hasAuthority('AUTORIZAR_DISPOSITIVO')")
    @GetMapping("/generate-token")
    public ResponseEntity<RegistroPontoTokenResponse> generatePontoJwtToken() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(new RegistroPontoTokenResponse(pontoJwtUtils.generateJwtToken(userDetails.getId())));
    }

    @PreAuthorize("hasAuthority('REGISTRAR_JORNADA')")
    @PostMapping("/validate-token")
    public ResponseEntity<Void> validatePontoJwtToken(@Valid @RequestBody RegistroPontoValidateTokenRequest registroPontoValidateTokenRequest) {
        if (pontoJwtUtils.validateJwtToken(registroPontoValidateTokenRequest.getToken()))
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.badRequest().build();
    }

    @PreAuthorize("hasAuthority('REGISTRAR_JORNADA')")
    @PostMapping("/{usuarioId}/logar")
    public ResponseEntity<Void> logar(@PathVariable("usuarioId") String usuarioId, @Valid @RequestBody RegistroJornadaLogarRequest registroJornadaLogarRequest) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException, RegistroPontoUnauthorizedException {
        if (!pontoJwtUtils.validateJwtToken(registroJornadaLogarRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario  = usuarioRepository.findByUsuarioId(usuarioService.parseUsuarioIdString(userDetails, usuarioId)).orElseThrow(NoSuchElementException::new);

        if (usuarioService.isUsuarioSupervisorOf(userDetails.getId(), usuario) || userDetails.hasAuthority("VER_TODAS_EQUIPES")) {
            registroJornadaService.logarBySupervisor(registroJornadaService.getRegistroJornadaByUsuarioIdHoje(usuario.getUsuarioId()));
            return ResponseEntity.noContent().build();
        }

        if (userDetails.getId() == usuario.getUsuarioId()) {
            registroJornadaService.logarByUsuario(registroJornadaService.getRegistroJornadaByUsuarioIdHoje(usuario.getUsuarioId()));
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PreAuthorize("hasAuthority('REGISTRAR_JORNADA')")
    @PostMapping("/{usuarioId}/deslogar")
    public ResponseEntity<Void> deslogar(@PathVariable("usuarioId") String usuarioId, @Valid @RequestBody RegistroJornadaLogarRequest registroJornadaLogarRequest) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException, RegistroPontoUnauthorizedException {
        if (!pontoJwtUtils.validateJwtToken(registroJornadaLogarRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario  = usuarioRepository.findByUsuarioId(usuarioService.parseUsuarioIdString(userDetails, usuarioId)).orElseThrow(NoSuchElementException::new);

        if (userDetails.getId() != usuario.getUsuarioId() && !usuarioService.isUsuarioSupervisorOf(userDetails.getId(), usuario) && !userDetails.hasAuthority("VER_TODAS_EQUIPES"))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        registroJornadaService.deslogar(registroJornadaService.getRegistroJornadaByUsuarioIdHoje(usuario.getUsuarioId()));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('REGISTRAR_JORNADA')")
    @PostMapping("/{usuarioId}/iniciar-hora-extra")
    public ResponseEntity<Void> iniciarHoraExtra(@PathVariable("usuarioId") String usuarioId, @Valid @RequestBody RegistroJornadaLogarRequest registroJornadaLogarRequest) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException, RegistroPontoUnauthorizedException {
        if (!pontoJwtUtils.validateJwtToken(registroJornadaLogarRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario  = usuarioRepository.findByUsuarioId(usuarioService.parseUsuarioIdString(userDetails, usuarioId)).orElseThrow(NoSuchElementException::new);

        if (userDetails.getId() != usuario.getUsuarioId() && !usuarioService.isUsuarioSupervisorOf(userDetails.getId(), usuario) && !userDetails.hasAuthority("VER_TODAS_EQUIPES"))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        registroJornadaService.iniciarHoraExtra(registroJornadaService.getRegistroJornadaByUsuarioIdHoje(usuario.getUsuarioId()));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('REGISTRAR_JORNADA')")
    @PostMapping("/{usuarioId}/toggle-hora-extra-permitida")
    public ResponseEntity<Void> toggleHoraExtraPermitida(@PathVariable("usuarioId") String usuarioId, @Valid @RequestBody RegistroJornadaLogarRequest registroJornadaLogarRequest) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException, RegistroPontoUnauthorizedException {
        if (!pontoJwtUtils.validateJwtToken(registroJornadaLogarRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario  = usuarioRepository.findByUsuarioId(usuarioService.parseUsuarioIdString(userDetails, usuarioId)).orElseThrow(NoSuchElementException::new);

        if (!usuarioService.isUsuarioSupervisorOf(userDetails.getId(), usuario) && !userDetails.hasAuthority("VER_TODAS_EQUIPES"))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        registroJornadaService.toggleHoraExtraPermitida(registroJornadaService.getRegistroJornadaByUsuarioIdHoje(usuario.getUsuarioId()));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('REGISTRAR_JORNADA')")
    @PostMapping("/{usuarioId}/toggle-hora-extra-auto")
    public ResponseEntity<Void> toggleHoraExtraAuto(@PathVariable("usuarioId") String usuarioId, @Valid @RequestBody RegistroJornadaLogarRequest registroJornadaLogarRequest) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException, RegistroPontoUnauthorizedException {
        if (!pontoJwtUtils.validateJwtToken(registroJornadaLogarRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario  = usuarioRepository.findByUsuarioId(usuarioService.parseUsuarioIdString(userDetails, usuarioId)).orElseThrow(NoSuchElementException::new);

        if (userDetails.getId() != usuario.getUsuarioId() && !usuarioService.isUsuarioSupervisorOf(userDetails.getId(), usuario) && !userDetails.hasAuthority("VER_TODAS_EQUIPES"))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        registroJornadaService.toggleHoraExtraAuto(registroJornadaService.getRegistroJornadaByUsuarioIdHoje(usuario.getUsuarioId()));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('REGISTRAR_JORNADA')")
    @PostMapping("/me/im-here")
    public ResponseEntity<Void> imHere(@Valid @RequestBody RegistroJornadaLogarRequest registroJornadaLogarRequest) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException, RegistroPontoUnauthorizedException {
        if (!pontoJwtUtils.validateJwtToken(registroJornadaLogarRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        registroJornadaService.imHere(registroJornadaService.getRegistroJornadaByUsuarioIdHoje(userDetails.getId()));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('REGISTRAR_JORNADA')")
    @PostMapping("/{usuarioId}/alterar-status")
    public ResponseEntity<Void> alterarStatusByMe(@PathVariable("usuarioId") String usuarioId, @Valid @RequestBody RegistroJornadaAlterarStatusRequest registroJornadaAlterarStatusRequest) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException, RegistroPontoUnauthorizedException, JornadaStatusNaoEncontradoException, JornadaStatusNaoPermitidoException {
        if (!pontoJwtUtils.validateJwtToken(registroJornadaAlterarStatusRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario  = usuarioRepository.findByUsuarioId(usuarioService.parseUsuarioIdString(userDetails, usuarioId)).orElseThrow(NoSuchElementException::new);

        if (usuarioService.isUsuarioSupervisorOf(userDetails.getId(), usuario) || userDetails.hasAuthority("VER_TODAS_EQUIPES")) {
            registroJornadaService.alterarStatusBySupervisor(registroJornadaService.getRegistroJornadaByUsuarioIdHoje(usuario.getUsuarioId()), registroJornadaAlterarStatusRequest.getJornadaStatusId());
            return ResponseEntity.noContent().build();
        }

        if (userDetails.getId() == usuario.getUsuarioId()) {
            registroJornadaService.alterarStatusByMe(registroJornadaService.getRegistroJornadaByUsuarioIdHoje(usuario.getUsuarioId()), registroJornadaAlterarStatusRequest.getJornadaStatusId());
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/{usuarioId}/report")
    public ResponseEntity<RegistroJornadaReportResponse> generateReportByUsuarioId(@PathVariable("usuarioId") Integer usuarioId, @Valid @RequestBody RegistroJornadaReportByUsuarioIdRequest registroJornadaReportByUsuarioIdRequest) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException, RegistroPontoUnauthorizedException, JornadaStatusNaoEncontradoException, JornadaStatusNaoPermitidoException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario  = usuarioRepository.findByUsuarioId(usuarioId).orElseThrow(NoSuchElementException::new);

        if (userDetails.getId() != usuario.getUsuarioId() && !usuarioService.isUsuarioSupervisorOf(userDetails.getId(), usuario) && !userDetails.hasAuthority("VER_TODAS_EQUIPES"))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        RegistroJornadaReportResponse registroJornadaReportResponse = new RegistroJornadaReportResponse();

        registroJornadaReportResponse.setAno(registroJornadaReportByUsuarioIdRequest.getAno());
        registroJornadaReportResponse.setMes(registroJornadaReportByUsuarioIdRequest.getMes());
        
        registroJornadaReportResponse.setUsuario(modelMapper.map(usuario, RegistroJornadaReportUsuarioResponse.class));

        List<RegistroJornadaReportDayResponse> dayList = new ArrayList<>();

        LocalDate startData = LocalDate.of(registroJornadaReportByUsuarioIdRequest.getAno(), registroJornadaReportByUsuarioIdRequest.getMes(), 1);
        LocalDate endData = startData.plusDays(startData.getMonth().length(Year.of(startData.getYear()).isLeap()));

        for (LocalDate data = startData; data.isBefore(endData); data = data.plusDays(1)) {
            RegistroJornadaReportDayResponse registroJornadaReportDayResponse = new RegistroJornadaReportDayResponse();
            registroJornadaReportDayResponse.setData(data);
            RegistroJornada registroJornada = registroJornadaRepository.findByUsuarioIdAndData(usuarioId, data).orElse(null);
            if (registroJornada != null) {
                registroJornadaReportDayResponse.setRegistroJornada(registroJornada);

                List<JornadaStatusGroupedResponse> jornadaStatusGroupedResponse = registroJornadaService.getStatusGroupedList(registroJornada);

                registroJornadaReportDayResponse.setStatusGroupedList(jornadaStatusGroupedResponse);

                registroJornadaReportDayResponse.setHorasATrabalhar(registroJornadaService.calculateHorasATrabalhar(registroJornada));
                registroJornadaReportDayResponse.setHorasTrabalhadas(registroJornadaService.calculateHorasTrabalhadas(jornadaStatusGroupedResponse));

                LocalTime entrada = registroJornadaService.calculateEntrada(registroJornada).orElse(null);
                LocalTime saida = registroJornadaService.calculateSaida(registroJornada).orElse(null);
                Integer horasNaoTrabalhadas = registroJornadaService.calculateHorasNaoTrabalhadas(jornadaStatusGroupedResponse, entrada, saida).orElse(null);

                registroJornadaReportDayResponse.setEntrada(entrada);
                registroJornadaReportDayResponse.setSaida(saida);
                registroJornadaReportDayResponse.setHorasNaoTrabalhadas(horasNaoTrabalhadas);
            }

            registroJornadaReportDayResponse.setRegistroJornadaCorrecao(registroJornadaCorrecaoRepository.findByRegistroJornadaCorrecaoKey(new RegistroJornadaCorrecaoKey(usuarioId, data)).orElse(null));
            
            dayList.add(registroJornadaReportDayResponse);
        }

        registroJornadaReportResponse.setDayList(dayList);
        
        return ResponseEntity.ok(registroJornadaReportResponse);
    }

}
