package br.net.unicom.backend.controller;

import org.modelmapper.ModelMapper;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import br.net.unicom.backend.model.PontoConfiguracao;
import br.net.unicom.backend.model.RegistroJornada;
import br.net.unicom.backend.model.exception.JornadaStatusNaoEncontradoException;
import br.net.unicom.backend.model.exception.JornadaStatusNaoPermitidoException;
import br.net.unicom.backend.model.exception.PontoConfiguracaoNaoEncontradoException;
import br.net.unicom.backend.model.exception.RegistroPontoUnauthorizedException;
import br.net.unicom.backend.model.exception.UsuarioNaoRegistraPontoHojeException;
import br.net.unicom.backend.model.exception.UsuarioSemContratoException;
import br.net.unicom.backend.model.exception.UsuarioSemJornadaException;
import br.net.unicom.backend.payload.request.RegistroJornadaAlterarStatusByMeRequest;
import br.net.unicom.backend.payload.request.RegistroJornadaLogarRequest;
import br.net.unicom.backend.payload.request.RegistroPontoValidateTokenRequest;
import br.net.unicom.backend.payload.response.RegistroJornadaResponse;
import br.net.unicom.backend.payload.response.RegistroPontoTokenResponse;
import br.net.unicom.backend.repository.JornadaExcecaoRepository;
import br.net.unicom.backend.repository.JornadaStatusRepository;
import br.net.unicom.backend.repository.PontoConfiguracaoRepository;
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
    PontoJwtUtils pontoJwtUtils;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ObjectMapper objectMapper;

    Logger logger = LoggerFactory.getLogger(RegistroPontoController.class);

    @PreAuthorize("hasAuthority('Jornada.Read.All')")
    @GetMapping("/me/hoje")
    public ResponseEntity<RegistroJornadaResponse> getRegistroJornadaByMeHoje() throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        PontoConfiguracao pontoConfiguracao = pontoConfiguracaoRepository.findByEmpresaId(userDetails.getEmpresaId()).orElseThrow(PontoConfiguracaoNaoEncontradoException::new);

        RegistroJornada registroJornada = registroJornadaService.getRegistroJornadaByUsuarioIdHoje(userDetails.getId());

        RegistroJornadaResponse registroJornadaResponse = modelMapper.map(registroJornada, RegistroJornadaResponse.class);

        registroJornadaResponse.setStatusAtual(registroJornadaService.getRegistroJornadaStatusAtual(registroJornada));

        registroJornadaResponse.setCanUsuarioLogar(registroJornadaService.canUsuarioLogar(registroJornada));

        registroJornadaResponse.setCanUsuarioIniciarHoraExtra(registroJornadaService.canUsuarioIniciarHoraExtra(registroJornada));

        registroJornadaResponse.setCanUsuarioDeslogar(registroJornadaService.canUsuarioDeslogar(registroJornada));

        registroJornadaResponse.setStatusGroupedList(registroJornadaService.getStatusGroupedList(registroJornada));

        registroJornadaResponse.setStatusOptionList(registroJornadaService.getStatusOptionList(registroJornada));

        registroJornadaResponse.setSecondsToAusente(registroJornadaService.getSecondsToAusente(registroJornada));

        registroJornadaResponse.setStatusRegularId(pontoConfiguracao.getStatusRegularId());

        registroJornadaResponse.setStatusHoraExtraId(pontoConfiguracao.getStatusHoraExtraId());

        return ResponseEntity.ok(registroJornadaResponse);
    }

    @PreAuthorize("hasAuthority('Jornada.Write.All')")
    @GetMapping("/generate-token")
    public ResponseEntity<RegistroPontoTokenResponse> generatePontoJwtToken() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(new RegistroPontoTokenResponse(pontoJwtUtils.generateJwtToken(userDetails.getId())));
    }

    @PreAuthorize("hasAuthority('Jornada.Read.All')")
    @PostMapping("/validate-token")
    public ResponseEntity<Void> validatePontoJwtToken(@Valid @RequestBody RegistroPontoValidateTokenRequest registroPontoValidateTokenRequest) {
        if (pontoJwtUtils.validateJwtToken(registroPontoValidateTokenRequest.getToken()))
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.badRequest().build();
    }

    @PreAuthorize("hasAuthority('Jornada.Read.All')")
    @PostMapping("/me/logar")
    public ResponseEntity<Void> logar(@Valid @RequestBody RegistroJornadaLogarRequest registroJornadaLogarRequest) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException, RegistroPontoUnauthorizedException {
        if (!pontoJwtUtils.validateJwtToken(registroJornadaLogarRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        registroJornadaService.logar(registroJornadaService.getRegistroJornadaByUsuarioIdHoje(userDetails.getId()));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Jornada.Read.All')")
    @PostMapping("/me/deslogar")
    public ResponseEntity<Void> deslogar(@Valid @RequestBody RegistroJornadaLogarRequest registroJornadaLogarRequest) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException, RegistroPontoUnauthorizedException {
        if (!pontoJwtUtils.validateJwtToken(registroJornadaLogarRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        registroJornadaService.deslogar(registroJornadaService.getRegistroJornadaByUsuarioIdHoje(userDetails.getId()));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Jornada.Read.All')")
    @PostMapping("/me/iniciar-hora-extra")
    public ResponseEntity<Void> iniciarHoraExtra(@Valid @RequestBody RegistroJornadaLogarRequest registroJornadaLogarRequest) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException, RegistroPontoUnauthorizedException {
        if (!pontoJwtUtils.validateJwtToken(registroJornadaLogarRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        registroJornadaService.iniciarHoraExtra(registroJornadaService.getRegistroJornadaByUsuarioIdHoje(userDetails.getId()));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Jornada.Read.All')")
    @PostMapping("/me/toggle-hora-extra-auto")
    public ResponseEntity<Void> horaExtraAuto(@Valid @RequestBody RegistroJornadaLogarRequest registroJornadaLogarRequest) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException, RegistroPontoUnauthorizedException {
        if (!pontoJwtUtils.validateJwtToken(registroJornadaLogarRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        registroJornadaService.toggleHoraExtraAuto(registroJornadaService.getRegistroJornadaByUsuarioIdHoje(userDetails.getId()));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Jornada.Read.All')")
    @PostMapping("/me/im-here")
    public ResponseEntity<Void> imHere(@Valid @RequestBody RegistroJornadaLogarRequest registroJornadaLogarRequest) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException, RegistroPontoUnauthorizedException {
        if (!pontoJwtUtils.validateJwtToken(registroJornadaLogarRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        registroJornadaService.imHere(registroJornadaService.getRegistroJornadaByUsuarioIdHoje(userDetails.getId()));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Jornada.Read.All')")
    @PostMapping("/me/alterar-status")
    public ResponseEntity<Void> alterarStatusByMe(@Valid @RequestBody RegistroJornadaAlterarStatusByMeRequest registroJornadaAlterarStatusByMeRequest) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException, PontoConfiguracaoNaoEncontradoException, RegistroPontoUnauthorizedException, JornadaStatusNaoEncontradoException, JornadaStatusNaoPermitidoException {
        if (!pontoJwtUtils.validateJwtToken(registroJornadaAlterarStatusByMeRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        registroJornadaService.alterarStatusByMe(registroJornadaService.getRegistroJornadaByUsuarioIdHoje(userDetails.getId()), registroJornadaAlterarStatusByMeRequest.getJornadaStatusId());
        return ResponseEntity.noContent().build();
    }

}
