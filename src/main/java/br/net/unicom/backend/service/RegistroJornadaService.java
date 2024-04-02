package br.net.unicom.backend.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.net.unicom.backend.model.Contrato;
import br.net.unicom.backend.model.Jornada;
import br.net.unicom.backend.model.JornadaExcecao;
import br.net.unicom.backend.model.JornadaStatus;
import br.net.unicom.backend.model.PontoConfiguracao;
import br.net.unicom.backend.model.RegistroJornada;
import br.net.unicom.backend.model.RegistroJornadaStatus;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.exception.JornadaStatusNaoEncontradoException;
import br.net.unicom.backend.model.exception.JornadaStatusNaoPermitidoException;
import br.net.unicom.backend.model.exception.PontoConfiguracaoNaoEncontradoException;
import br.net.unicom.backend.model.exception.UsuarioNaoRegistraPontoHojeException;
import br.net.unicom.backend.model.exception.UsuarioSemContratoException;
import br.net.unicom.backend.model.exception.UsuarioSemJornadaException;
import br.net.unicom.backend.payload.response.JornadaStatusGroupedResponse;
import br.net.unicom.backend.payload.response.JornadaStatusOptionResponse;
import br.net.unicom.backend.payload.response.RegistroJornadaStatusAtualResponse;
import br.net.unicom.backend.repository.JornadaExcecaoRepository;
import br.net.unicom.backend.repository.JornadaStatusRepository;
import br.net.unicom.backend.repository.PontoConfiguracaoRepository;
import br.net.unicom.backend.repository.RegistroJornadaRepository;
import br.net.unicom.backend.repository.RegistroJornadaStatusRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RegistroJornadaService {

    Logger logger = LoggerFactory.getLogger(RegistroPontoService.class);

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    JornadaExcecaoRepository jornadaExcecaoRepository;

    @Autowired
    JornadaStatusRepository jornadaStatusRepository;

    @Autowired
    RegistroJornadaStatusRepository registroJornadaStatusRepository;

    @Autowired
    PontoConfiguracaoRepository pontoConfiguracaoRepository;

    @Autowired
    RegistroJornadaRepository registroJornadaRepository;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ModelMapper modelMapper;

    public RegistroJornada getRegistroJornadaByUsuarioIdHoje(Integer usuarioId) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException {

        LocalDate hoje = LocalDate.now();

        RegistroJornada registroJornada = registroJornadaRepository.findByUsuarioIdAndData(usuarioId, hoje).orElse(null);

        if (registroJornada == null)
            registroJornada = createRegistroJornadaByUsuarioIdAndData(usuarioId, hoje);

        return registroJornada;
    }

    public RegistroJornada createRegistroJornadaByUsuarioIdAndData(Integer usuarioId, LocalDate data) throws UsuarioSemContratoException, UsuarioSemJornadaException, UsuarioNaoRegistraPontoHojeException {

        Usuario usuario = usuarioRepository.findByUsuarioId(usuarioId).get();

        RegistroJornada registroJornada;

        Contrato contrato = Optional.ofNullable(usuario.getContrato()).orElseThrow(UsuarioSemContratoException::new);

        Optional<JornadaExcecao> jornadaExcecao = jornadaExcecaoRepository.findByUsuarioIdAndData(usuario.getUsuarioId(), data);

        if (jornadaExcecao.isPresent()) {

            if (jornadaExcecao.get().getRegistraPonto() == false)
                throw new UsuarioNaoRegistraPontoHojeException();

            registroJornada = new RegistroJornada();
            registroJornada.setUsuarioId(usuario.getUsuarioId());
            registroJornada.setUsuario(usuario);
            registroJornada.setContratoNome(contrato.getNome());
            registroJornada.setData(data);
            registroJornada.setJornadaEntrada(jornadaExcecao.get().getEntrada());
            registroJornada.setJornadaIntervaloInicio(jornadaExcecao.get().getIntervaloInicio());
            registroJornada.setJornadaIntervaloFim(jornadaExcecao.get().getIntervaloFim());
            registroJornada.setJornadaSaida(jornadaExcecao.get().getSaida());
            registroJornada.setHoraExtraAuto(false);
            registroJornada.setEmHoraExtra(false);
            registroJornadaRepository.save(registroJornada);

        } else {

            Jornada jornada = Optional.ofNullable(usuario.getJornada()).orElseThrow(UsuarioSemJornadaException::new);

            Boolean registraPontoHoje = true;
            switch (data.getDayOfWeek().getValue()) {
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

            registroJornada = new RegistroJornada();
            registroJornada.setUsuarioId(usuario.getUsuarioId());
            registroJornada.setUsuario(usuario);
            registroJornada.setContratoNome(contrato.getNome());
            registroJornada.setData(data);
            registroJornada.setJornadaEntrada(jornada.getEntrada());
            registroJornada.setJornadaIntervaloInicio(jornada.getIntervaloInicio());
            registroJornada.setJornadaIntervaloFim(jornada.getIntervaloFim());
            registroJornada.setJornadaSaida(jornada.getSaida());
            registroJornada.setHoraExtraAuto(false);
            registroJornada.setEmHoraExtra(false);
            registroJornadaRepository.save(registroJornada);
        }
        
        return registroJornada;
    }

    public RegistroJornadaStatusAtualResponse getRegistroJornadaStatusAtual(RegistroJornada registroJornada) {
        RegistroJornadaStatus statusAtual = registroJornada.getStatusAtual();

        if (statusAtual == null)
            return null;

        RegistroJornadaStatusAtualResponse registroJornadaStatusAtualResponse = modelMapper.map(statusAtual, RegistroJornadaStatusAtualResponse.class);
        modelMapper.map(statusAtual.getJornadaStatus(), registroJornadaStatusAtualResponse);
        if (statusAtual.getJornadaStatus().getAgrupar())
            registroJornadaStatusAtualResponse.setDuracao(this.getRegistroJornadaStatusDuracaoTotal(statusAtual));
        else
            registroJornadaStatusAtualResponse.setDuracao(this.getRegistroJornadaStatusDuracao(statusAtual));
        return registroJornadaStatusAtualResponse;
    }

    public Integer getRegistroJornadaStatusDuracao(RegistroJornadaStatus registroJornadaStatus) {
        if (registroJornadaStatus.getFim() != null)
            return (int) Duration.between(registroJornadaStatus.getInicio(), registroJornadaStatus.getFim()).toSeconds();
        else
            return (int) Duration.between(registroJornadaStatus.getInicio(), LocalTime.now()).toSeconds();
    }

    public Integer getRegistroJornadaStatusDuracaoTotal(RegistroJornadaStatus registroJornadaStatus) {
        return registroJornadaStatusRepository.getDuracaoSumByJornadaStatusIdAndRegistroJornadaId(registroJornadaStatus.getJornadaStatusId(), registroJornadaStatus.getRegistroJornadaId()).orElse(0);
    }

    public Integer getRegistroJornadaStatusDuracaoTotal(Integer jornadaStatusId, Integer registroJornadaStatusId) {
        return registroJornadaStatusRepository.getDuracaoSumByJornadaStatusIdAndRegistroJornadaId(jornadaStatusId, registroJornadaStatusId).orElse(0);
    }

    public Integer getRegistroJornadaStatusUsosTotal(RegistroJornada registroJornada, JornadaStatus jornadaStatus) {
        return registroJornadaStatusRepository.getUsosByRegistroJornadaIdAndJornadaStatusId(registroJornada.getRegistroJornadaId(), jornadaStatus.getJornadaStatusId()).orElse(0);
    }

    public Boolean existsRegistroJornadaStatus(RegistroJornada registroJornada) {
        return registroJornadaStatusRepository.existsByRegistroJornadaId(registroJornada.getRegistroJornadaId());
    }

    public Boolean canUsuarioLogar(RegistroJornada registroJornada) throws PontoConfiguracaoNaoEncontradoException {
        if (registroJornada.getStatusAtualId() != null)
            return false;
        //if (existsRegistroJornadaStatus(registroJornada))
            //return false;
        if (!isInRegularTime(registroJornada) && !isInHoraExtraTime(registroJornada))
            return false;
        return true;
    }

    public Boolean canUsuarioDeslogar(RegistroJornada registroJornada) {
        return (registroJornada.getStatusAtualId() != null);
    }

    public Boolean canUsuarioIniciarHoraExtra(RegistroJornada registroJornada) throws PontoConfiguracaoNaoEncontradoException {
        if (!isInHoraExtraTime(registroJornada))
            return false;
        if (registroJornada.getStatusAtualId() != null && registroJornada.getEmHoraExtra())
            return false;
        return true;
    }

    public Boolean isInRegularTime(RegistroJornada registroJornada) {
        LocalTime now = LocalTime.now();

        return (now.compareTo(registroJornada.getJornadaEntrada()) >= 0 && now.compareTo(registroJornada.getJornadaSaida()) <=0);
    }

    public Boolean isInHoraExtraTime(RegistroJornada registroJornada) throws PontoConfiguracaoNaoEncontradoException {
        if (isInRegularTime(registroJornada))
            return false;

        LocalTime now = LocalTime.now();

        PontoConfiguracao pontoConfiguracao = pontoConfiguracaoRepository.findByEmpresaId(registroJornada.getUsuario().getEmpresaId()).orElseThrow(PontoConfiguracaoNaoEncontradoException::new);

        long beforeDuration = Duration.between(now, registroJornada.getJornadaEntrada()).toSeconds();
        long afterDuration = Duration.between(registroJornada.getJornadaSaida(), now).toSeconds();

        return ((beforeDuration < pontoConfiguracao.getHoraExtraMax() && beforeDuration >= 0) ||
                (afterDuration < pontoConfiguracao.getHoraExtraMax() && afterDuration >= 0));
    }

    public void alterarStatus(RegistroJornada registroJornada, JornadaStatus novoStatus) {

        LocalTime agora = LocalTime.now();

        RegistroJornadaStatus novoRegistroJornadaStatus = null;
        
        if (novoStatus != null) {
            novoRegistroJornadaStatus = new RegistroJornadaStatus();
            novoRegistroJornadaStatus.setRegistroJornadaId(registroJornada.getRegistroJornadaId());
            novoRegistroJornadaStatus.setJornadaStatusId(novoStatus.getJornadaStatusId());
            novoRegistroJornadaStatus.setInicio(agora);
            registroJornadaStatusRepository.save(novoRegistroJornadaStatus);
        }

        if (registroJornada.getStatusAtual() != null) {
            registroJornada.getStatusAtual().setFim(agora);
            registroJornadaStatusRepository.save(registroJornada.getStatusAtual());
        }

        if (novoStatus == null)
            registroJornada.setStatusAtualId(null);
        else
            registroJornada.setStatusAtualId(novoRegistroJornadaStatus.getRegistroJornadaStatusId());
        registroJornadaRepository.save(registroJornada);
    }

    public void alterarStatusByMe(RegistroJornada registroJornada, Integer jornadaStatusId) throws JornadaStatusNaoEncontradoException, JornadaStatusNaoPermitidoException, PontoConfiguracaoNaoEncontradoException {

        PontoConfiguracao pontoConfiguracao = pontoConfiguracaoRepository.findByEmpresaId(registroJornada.getUsuario().getEmpresaId()).orElseThrow(PontoConfiguracaoNaoEncontradoException::new);

        JornadaStatus novoStatus = jornadaStatusRepository.findByJornadaStatusIdAndEmpresaId(jornadaStatusId, registroJornada.getUsuario().getEmpresaId()).orElseThrow(JornadaStatusNaoEncontradoException::new);

        if (novoStatus.getJornadaStatusId() == registroJornada.getStatusAtual().getJornadaStatusId())
                throw new JornadaStatusNaoPermitidoException("novo status não pode ser igual ao atual");

        if (!registroJornada.getStatusAtual().getJornadaStatus().getUsuarioPodeAtivar() &&
            registroJornada.getStatusAtual().getJornadaStatusId() != pontoConfiguracao.getStatusRegularId() &&
            registroJornada.getStatusAtual().getJornadaStatusId() != pontoConfiguracao.getStatusHoraExtraId())
            throw new JornadaStatusNaoPermitidoException("usuário não pode ativar o status atual");

        if (novoStatus.getJornadaStatusId() == pontoConfiguracao.getStatusRegularId()) {

            if (registroJornada.getEmHoraExtra())
                throw new JornadaStatusNaoPermitidoException("está em hora extra");
                
            alterarStatus(registroJornada, pontoConfiguracao.getStatusRegular());

        } else if (novoStatus.getJornadaStatusId() == pontoConfiguracao.getStatusHoraExtraId()) {

            if (!registroJornada.getEmHoraExtra())
                throw new JornadaStatusNaoPermitidoException("bão está em hora extra");

            alterarStatus(registroJornada, pontoConfiguracao.getStatusHoraExtra());
        } else {

            if (!novoStatus.getUsuarioPodeAtivar())
                throw new JornadaStatusNaoPermitidoException("usuário não pode ativar o status novo");

            if (novoStatus.getMaxUso() != null && getRegistroJornadaStatusUsosTotal(registroJornada, novoStatus) >= novoStatus.getMaxUso())
                throw new JornadaStatusNaoPermitidoException("max. de uso atingido");

            alterarStatus(registroJornada, novoStatus);
        }
        
    }

    public void logar(RegistroJornada registroJornada) throws PontoConfiguracaoNaoEncontradoException {
        if (!canUsuarioLogar(registroJornada))
            return;

        PontoConfiguracao pontoConfiguracao = pontoConfiguracaoRepository.findByEmpresaId(registroJornada.getUsuario().getEmpresaId()).orElseThrow(PontoConfiguracaoNaoEncontradoException::new);

        if (isInRegularTime(registroJornada)) {
            alterarStatus(registroJornada, pontoConfiguracao.getStatusRegular());
            registroJornada.setEmHoraExtra(false);
        } else if (isInHoraExtraTime(registroJornada)) {
            alterarStatus(registroJornada, pontoConfiguracao.getStatusHoraExtra());
            registroJornada.setEmHoraExtra(true);
        }

        registroJornadaRepository.save(registroJornada);
    }

    public void deslogar(RegistroJornada registroJornada) {
        if (canUsuarioDeslogar(registroJornada))
            alterarStatus(registroJornada, null);
    }

    public void iniciarHoraExtra(RegistroJornada registroJornada) throws PontoConfiguracaoNaoEncontradoException {
        PontoConfiguracao pontoConfiguracao = pontoConfiguracaoRepository.findByEmpresaId(registroJornada.getUsuario().getEmpresaId()).orElseThrow(PontoConfiguracaoNaoEncontradoException::new);

        if (canUsuarioIniciarHoraExtra(registroJornada) && registroJornada.getStatusAtual() != null) {
            registroJornada.setEmHoraExtra(true);
            registroJornadaRepository.saveAndFlush(registroJornada);
            if (registroJornada.getStatusAtual().getJornadaStatusId() == pontoConfiguracao.getStatusRegularId())
                alterarStatus(registroJornada, pontoConfiguracao.getStatusHoraExtra());
        }
    }

    public void iniciarRegular(RegistroJornada registroJornada) throws PontoConfiguracaoNaoEncontradoException {
        PontoConfiguracao pontoConfiguracao = pontoConfiguracaoRepository.findByEmpresaId(registroJornada.getUsuario().getEmpresaId()).orElseThrow(PontoConfiguracaoNaoEncontradoException::new);

        if (isInRegularTime(registroJornada) && registroJornada.getStatusAtual() != null) {
            registroJornada.setEmHoraExtra(false);
            registroJornadaRepository.saveAndFlush(registroJornada);
            if (registroJornada.getStatusAtual().getJornadaStatusId() == pontoConfiguracao.getStatusHoraExtraId())
                alterarStatus(registroJornada, pontoConfiguracao.getStatusRegular());
        }
    }

    public List<JornadaStatusGroupedResponse> getStatusGroupedList(RegistroJornada registroJornada) {
        return jornadaStatusRepository.getJornadaStatusGroupedProjectionListByRegistroJornadaId(registroJornada.getRegistroJornadaId())
            .stream()
            .map(statusGrouped -> modelMapper.map(statusGrouped, JornadaStatusGroupedResponse.class))
            .collect(Collectors.toList());
    }

    public List<JornadaStatusOptionResponse> getStatusOptionList(RegistroJornada registroJornada) {
        return jornadaStatusRepository.getJornadaStatusOptionProjectionListByEmpresaIdAndRegistroJornadaId(registroJornada.getUsuario().getEmpresaId(), registroJornada.getRegistroJornadaId())
            .stream()
            .map(statusGrouped -> modelMapper.map(statusGrouped, JornadaStatusOptionResponse.class))
            .collect(Collectors.toList());
    }

    public void toggleHoraExtraAuto(RegistroJornada registroJornada) {
        registroJornada.setHoraExtraAuto(!registroJornada.getHoraExtraAuto());
    }

    public Integer getSecondsToAusente(RegistroJornada registroJornada) throws PontoConfiguracaoNaoEncontradoException {
        PontoConfiguracao pontoConfiguracao = pontoConfiguracaoRepository.findByEmpresaId(registroJornada.getUsuario().getEmpresaId()).orElseThrow(PontoConfiguracaoNaoEncontradoException::new);
        Usuario usuario = registroJornada.getUsuario();

        if (registroJornada.getStatusAtual() == null)
            return -1;
        if (registroJornada.getStatusAtual().getJornadaStatusId() == pontoConfiguracao.getStatusAusenteId())
            return 0;
        if (registroJornada.getStatusAtual().getJornadaStatusId() != pontoConfiguracao.getStatusRegularId() &&
            registroJornada.getStatusAtual().getJornadaStatusId() != pontoConfiguracao.getStatusHoraExtraId())
            return -1;

        Integer secondsSinceVistoPorUltimo = (int) usuarioService.getDurationSinceUsuarioVistoPorUltimo(registroJornada.getUsuario()).toSeconds();

        if (!registroJornada.getEmHoraExtra()) {
            return Math.max(pontoConfiguracao.getIntervaloVerificacaoRegular() - secondsSinceVistoPorUltimo, 0);
        } else {
            return Math.max(pontoConfiguracao.getIntervaloVerificacaoHoraExtra() - secondsSinceVistoPorUltimo, 0);
        }
    }

    public void imHere(RegistroJornada registroJornada) throws PontoConfiguracaoNaoEncontradoException {
        PontoConfiguracao pontoConfiguracao = pontoConfiguracaoRepository.findByEmpresaId(registroJornada.getUsuario().getEmpresaId()).orElseThrow(PontoConfiguracaoNaoEncontradoException::new);
        Usuario usuario = registroJornada.getUsuario();

        usuarioService.ping(usuario);
        if (registroJornada.getStatusAtual() != null) {
            if (registroJornada.getStatusAtual().getJornadaStatusId() == pontoConfiguracao.getStatusAusenteId()) {
                if (!registroJornada.getEmHoraExtra()) {
                    alterarStatus(registroJornada, pontoConfiguracao.getStatusRegular());
                } else {
                    alterarStatus(registroJornada, pontoConfiguracao.getStatusHoraExtra());
                }
            }
        }
    }

    private void gerenciarLogado(RegistroJornada registroJornada) {
        try {
            PontoConfiguracao pontoConfiguracao = pontoConfiguracaoRepository.findByEmpresaId(registroJornada.getUsuario().getEmpresaId()).orElseThrow(PontoConfiguracaoNaoEncontradoException::new);
            Usuario usuario = registroJornada.getUsuario();

            if (registroJornada.getStatusAtual() == null) {
                if (!existsRegistroJornadaStatus(registroJornada) &&
                    isInRegularTime(registroJornada) &&
                    Duration.between(usuario.getVistoPorUltimo(), LocalDateTime.now()).toSeconds() < 60) {
                        logar(registroJornada);
                    }
            } else {
                if (!registroJornada.getEmHoraExtra()) {
                    if (isInHoraExtraTime(registroJornada) && registroJornada.getHoraExtraAuto()) {
                        iniciarHoraExtra(registroJornada);
                    } else if (Duration.between(registroJornada.getJornadaSaida(), LocalTime.now()).toSeconds() > 300) {
                        deslogar(registroJornada);
                    }
                } else {
                    if (!isInHoraExtraTime(registroJornada)) {
                        if (isInRegularTime(registroJornada)) {
                            iniciarRegular(registroJornada);
                        } else {
                            deslogar(registroJornada);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void gerenciarAusencia(RegistroJornada registroJornada) {
        try {
                LocalDateTime agora = LocalDateTime.now();
                PontoConfiguracao pontoConfiguracao = pontoConfiguracaoRepository.findByEmpresaId(registroJornada.getUsuario().getEmpresaId()).orElseThrow(PontoConfiguracaoNaoEncontradoException::new);
                Usuario usuario = registroJornada.getUsuario();

                if (registroJornada.getStatusAtual() == null)
                    return;
                if (!registroJornada.getEmHoraExtra()) {
                    if (registroJornada.getStatusAtual().getJornadaStatusId() == pontoConfiguracao.getStatusRegularId()) {
                        if (Duration.between(usuario.getVistoPorUltimo(), agora).toSeconds() > pontoConfiguracao.getIntervaloVerificacaoRegular()) {
                            alterarStatus(registroJornada, pontoConfiguracao.getStatusAusente());
                        }
                    } else if (registroJornada.getStatusAtual().getJornadaStatusId() == pontoConfiguracao.getStatusAusenteId()) {
                        if (Duration.between(usuario.getVistoPorUltimo(), agora).toSeconds() <= pontoConfiguracao.getIntervaloVerificacaoRegular()) {
                            alterarStatus(registroJornada, pontoConfiguracao.getStatusRegular());
                        }
                    }
                } else {
                    if (registroJornada.getStatusAtual().getJornadaStatusId() == pontoConfiguracao.getStatusHoraExtraId()) {
                        if (Duration.between(usuario.getVistoPorUltimo(), agora).toSeconds() > pontoConfiguracao.getIntervaloVerificacaoHoraExtra()) {
                            alterarStatus(registroJornada, pontoConfiguracao.getStatusAusente());
                        }
                    } else if (registroJornada.getStatusAtual().getJornadaStatusId() == pontoConfiguracao.getStatusAusenteId()) {
                        if (Duration.between(usuario.getVistoPorUltimo(), agora).toSeconds() <= pontoConfiguracao.getIntervaloVerificacaoHoraExtra()) {
                            alterarStatus(registroJornada, pontoConfiguracao.getStatusHoraExtra());
                        }
                    }
                }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void gerenciarUsuarios() {
        LocalDate hoje = LocalDate.now();

        List<RegistroJornada> registroJornadaList = registroJornadaRepository.findAllByData(hoje);

        registroJornadaList.forEach(registroJornada -> {
            try {
                gerenciarLogado(registroJornada);
                gerenciarAusencia(registroJornada);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });

    }

}