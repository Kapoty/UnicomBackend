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
import br.net.unicom.backend.repository.JornadaRepository;
import br.net.unicom.backend.repository.JornadaStatusRepository;
import br.net.unicom.backend.repository.PontoConfiguracaoRepository;
import br.net.unicom.backend.repository.RegistroJornadaRepository;
import br.net.unicom.backend.repository.RegistroJornadaStatusRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RegistroJornadaService {

    Logger logger = LoggerFactory.getLogger(RegistroPontoService.class);

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    JornadaRepository jornadaRepository;

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

    @Autowired
    EntityManager entityManager;

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

        Optional<Jornada> jornada = jornadaRepository.findByUsuarioIdAndData(usuario.getUsuarioId(), data);

        if (jornada.isPresent()) {

            if (jornada.get().getEntrada() == null)
                throw new UsuarioNaoRegistraPontoHojeException();

            registroJornada = new RegistroJornada();
            registroJornada.setUsuarioId(usuario.getUsuarioId());
            registroJornada.setUsuario(usuario);
            registroJornada.setContratoId(contrato.getContratoId());
            registroJornada.setContrato(contrato);
            registroJornada.setData(data);
            registroJornada.setJornadaEntrada(jornada.get().getEntrada());
            registroJornada.setJornadaIntervaloInicio(jornada.get().getIntervaloInicio());
            registroJornada.setJornadaIntervaloFim(jornada.get().getIntervaloFim());
            registroJornada.setJornadaSaida(jornada.get().getSaida());
            registroJornada.setHoraExtraPermitida(false);
            registroJornadaRepository.save(registroJornada);

            logger.info("RegistroJornada do usuarioId %d criado com sucesso!".formatted(usuario.getUsuarioId()));

        } else {
            throw new UsuarioSemJornadaException();
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
        if (existsRegistroJornadaStatus(registroJornada))
            return false;
        if (LocalTime.now().isAfter(LocalTime.of(23, 55)))
            return false;
        return true;
    }

    public Boolean canSupervisorLogar(RegistroJornada registroJornada) throws PontoConfiguracaoNaoEncontradoException {
        if (registroJornada.getStatusAtualId() != null)
            return false;
        if (LocalTime.now().isAfter(LocalTime.of(23, 55)))
            return false;
        return true;
    }

    public Boolean canUsuarioDeslogar(RegistroJornada registroJornada) {
        return (registroJornada.getStatusAtualId() != null);
    }

    public void alterarStatus(RegistroJornada registroJornada, JornadaStatus novoStatus) {

        LocalTime agora = LocalTime.now();

        RegistroJornadaStatus novoRegistroJornadaStatus = null;

        if (registroJornada.getStatusAtual() != null) {
            registroJornada.getStatusAtual().setFim(agora);
            registroJornadaStatusRepository.saveAndFlush(registroJornada.getStatusAtual());
        }
        
        if (novoStatus != null) {
            novoRegistroJornadaStatus = new RegistroJornadaStatus();
            novoRegistroJornadaStatus.setRegistroJornadaId(registroJornada.getRegistroJornadaId());
            novoRegistroJornadaStatus.setJornadaStatusId(novoStatus.getJornadaStatusId());
            novoRegistroJornadaStatus.setInicio(agora);
            registroJornadaStatusRepository.save(novoRegistroJornadaStatus);
        }

        if (novoRegistroJornadaStatus == null)
            registroJornada.setStatusAtualId(null);
        else
            registroJornada.setStatusAtualId(novoRegistroJornadaStatus.getRegistroJornadaStatusId());
        registroJornadaRepository.save(registroJornada);
    }

    public void alterarStatusByMe(RegistroJornada registroJornada, Integer jornadaStatusId) throws JornadaStatusNaoEncontradoException, JornadaStatusNaoPermitidoException, PontoConfiguracaoNaoEncontradoException {

        JornadaStatus novoStatus = jornadaStatusRepository.findByJornadaStatusIdAndEmpresaIdAndJornadaStatusGrupoId(jornadaStatusId, registroJornada.getUsuario().getEmpresaId(), registroJornada.getUsuario().getJornadaStatusGrupoId()).orElseThrow(JornadaStatusNaoEncontradoException::new);

        if (novoStatus.getJornadaStatusId().equals(registroJornada.getStatusAtual().getJornadaStatusId()))
                throw new JornadaStatusNaoPermitidoException("novo status não pode ser igual ao atual");

        if (!registroJornada.getStatusAtual().getJornadaStatus().getUsuarioPodeAtivar())
            throw new JornadaStatusNaoPermitidoException("usuário não pode ativar o status atual");

        if (!novoStatus.getUsuarioPodeAtivar())
            throw new JornadaStatusNaoPermitidoException("usuário não pode ativar o status novo");

        if (novoStatus.getMaxUso() != null && getRegistroJornadaStatusUsosTotal(registroJornada, novoStatus) >= novoStatus.getMaxUso())
            throw new JornadaStatusNaoPermitidoException("max. de uso atingido");

        alterarStatus(registroJornada, novoStatus);

    }

    public void alterarStatusBySupervisor(RegistroJornada registroJornada, Integer jornadaStatusId) throws JornadaStatusNaoEncontradoException, JornadaStatusNaoPermitidoException, PontoConfiguracaoNaoEncontradoException {

        JornadaStatus novoStatus = jornadaStatusRepository.findByJornadaStatusIdAndEmpresaIdAndJornadaStatusGrupoId(jornadaStatusId, registroJornada.getUsuario().getEmpresaId(), registroJornada.getUsuario().getJornadaStatusGrupoId()).orElseThrow(JornadaStatusNaoEncontradoException::new);

        if (novoStatus.getJornadaStatusId().equals(registroJornada.getStatusAtual().getJornadaStatusId()))
                throw new JornadaStatusNaoPermitidoException("novo status não pode ser igual ao atual");

        if (!novoStatus.getSupervisorPodeAtivar())
            throw new JornadaStatusNaoPermitidoException("supervisor não pode ativar o status novo");

        alterarStatus(registroJornada, novoStatus);
        
    }

    public void logar(RegistroJornada registroJornada) throws PontoConfiguracaoNaoEncontradoException {

        PontoConfiguracao pontoConfiguracao = pontoConfiguracaoRepository.findByEmpresaId(registroJornada.getUsuario().getEmpresaId()).orElseThrow(PontoConfiguracaoNaoEncontradoException::new);

        alterarStatus(registroJornada, pontoConfiguracao.getStatusRegular());

    }

    public void logarBySupervisor(RegistroJornada registroJornada) throws PontoConfiguracaoNaoEncontradoException {
        if (canSupervisorLogar(registroJornada))
            logar(registroJornada);
    }

    public void logarByUsuario(RegistroJornada registroJornada) throws PontoConfiguracaoNaoEncontradoException {
        if (canUsuarioLogar(registroJornada))
            logar(registroJornada);
    }

    public void deslogar(RegistroJornada registroJornada) {
        if (canUsuarioDeslogar(registroJornada))
            alterarStatus(registroJornada, null);
    }

    public List<JornadaStatusGroupedResponse> getStatusGroupedList(RegistroJornada registroJornada) {
        return jornadaStatusRepository.getJornadaStatusGroupedProjectionListByRegistroJornadaId(registroJornada.getRegistroJornadaId())
            .stream()
            .map(statusGrouped -> modelMapper.map(statusGrouped, JornadaStatusGroupedResponse.class))
            .collect(Collectors.toList());
    }

    public List<JornadaStatusOptionResponse> getStatusOptionList(RegistroJornada registroJornada) {
        return jornadaStatusRepository.getJornadaStatusOptionProjectionListByEmpresaIdAndJornadaStatusGrupoIdAndRegistroJornadaId(registroJornada.getUsuario().getEmpresaId(), registroJornada.getUsuario().getJornadaStatusGrupoId(), registroJornada.getRegistroJornadaId())
            .stream()
            .map(statusGrouped -> modelMapper.map(statusGrouped, JornadaStatusOptionResponse.class))
            .collect(Collectors.toList());
    }

    public void toggleHoraExtraPermitida(RegistroJornada registroJornada) {
        registroJornada.setHoraExtraPermitida(!registroJornada.getHoraExtraPermitida());
    }

    public Integer getSecondsToAusente(RegistroJornada registroJornada) throws PontoConfiguracaoNaoEncontradoException {
        PontoConfiguracao pontoConfiguracao = pontoConfiguracaoRepository.findByEmpresaId(registroJornada.getUsuario().getEmpresaId()).orElseThrow(PontoConfiguracaoNaoEncontradoException::new);

        if (registroJornada.getStatusAtual() == null)
            return -1;
        if (registroJornada.getStatusAtual().getJornadaStatusId().equals(pontoConfiguracao.getStatusAusenteId()))
            return 0;
        if (!registroJornada.getStatusAtual().getJornadaStatusId().equals(pontoConfiguracao.getStatusRegularId()))
            return -1;

        Integer secondsSinceVistoPorUltimo = (int) usuarioService.getDurationSinceUsuarioVistoPorUltimo(registroJornada.getUsuario()).toSeconds();

        if (!this.isEmHoraExtra(registroJornada)) {
            return Math.max(pontoConfiguracao.getIntervaloVerificacaoRegular() - secondsSinceVistoPorUltimo, 0);
        } else {
            return Math.max(pontoConfiguracao.getIntervaloVerificacaoHoraExtra() - secondsSinceVistoPorUltimo, 0);
        }
    }

    public Boolean isInRegularTime(RegistroJornada registroJornada) {
        LocalTime now = LocalTime.now();

        return (now.compareTo(registroJornada.getJornadaEntrada()) >= 0 && now.compareTo(registroJornada.getJornadaSaida()) <=0);
    }

    public Boolean isEmHoraExtra(RegistroJornada registroJornada) {
        return (this.calculateHorasTrabalhadas(registroJornada) > this.calculateHorasATrabalhar(registroJornada));
    }

    public Integer calculateHorasIntervalo(RegistroJornada registroJornada) {
        return (int) (Duration.between(registroJornada.getJornadaIntervaloInicio(), registroJornada.getJornadaIntervaloFim()).toSeconds());
    }

    public Integer calculateHorasJornada(RegistroJornada registroJornada) {
        return (int) (Duration.between(registroJornada.getJornadaEntrada(), registroJornada.getJornadaSaida()).toSeconds());
    }

    public Integer calculateHorasATrabalhar(RegistroJornada registroJornada) {
        return (calculateHorasJornada(registroJornada) - calculateHorasIntervalo(registroJornada));
    }

    public Integer calculateHorasTrabalhadas(RegistroJornada registroJornada) {
        List<JornadaStatusGroupedResponse> statusGroupedList = this.getStatusGroupedList(registroJornada);
        return calculateHorasTrabalhadas(statusGroupedList);
    }

    public Integer calculateHorasTrabalhadas(List<JornadaStatusGroupedResponse> statusGroupedList) {
        Integer horasTrabalhadas = 0;

        for (JornadaStatusGroupedResponse jornadaStatusGroupedResponse : statusGroupedList) {
            if (jornadaStatusGroupedResponse.getHoraTrabalhada()) {
                if (jornadaStatusGroupedResponse.getMaxDuracao() == null || jornadaStatusGroupedResponse.getMaxUso() == null)
                    horasTrabalhadas += jornadaStatusGroupedResponse.getDuracao();
                else {
                    horasTrabalhadas += Math.min(jornadaStatusGroupedResponse.getDuracao(), jornadaStatusGroupedResponse.getMaxDuracao() * jornadaStatusGroupedResponse.getMaxUso()); 
                }
            }
        }

        return horasTrabalhadas;
    }

    public Optional<Integer> calculateHorasNaoTrabalhadas(List<JornadaStatusGroupedResponse> statusGroupedList, LocalTime entrada, LocalTime saida) {
        if (entrada == null || saida == null)
            return Optional.empty();

        Integer horasTrabalhadas = calculateHorasTrabalhadas(statusGroupedList);
        
        return Optional.of((int) Duration.between(entrada, saida).toSeconds() - horasTrabalhadas);
    } 

    public Optional<LocalTime> calculateEntrada(RegistroJornada registroJornada) {
        return registroJornadaStatusRepository.calculateEntradaByRegistroJornadaId(registroJornada.getRegistroJornadaId());
    }

    public Optional<LocalTime> calculateSaida(RegistroJornada registroJornada) {
        return registroJornadaStatusRepository.calculateSaidaByRegistroJornadaId(registroJornada.getRegistroJornadaId());
    }

    public void imHere(RegistroJornada registroJornada) throws PontoConfiguracaoNaoEncontradoException {
        PontoConfiguracao pontoConfiguracao = pontoConfiguracaoRepository.findByEmpresaId(registroJornada.getUsuario().getEmpresaId()).orElseThrow(PontoConfiguracaoNaoEncontradoException::new);
        Usuario usuario = registroJornada.getUsuario();

        usuarioService.ping(usuario);
        if (registroJornada.getStatusAtual() != null && registroJornada.getStatusAtual().getJornadaStatusId().equals(pontoConfiguracao.getStatusAusenteId())) {
            alterarStatus(registroJornada, pontoConfiguracao.getStatusRegular());
        }
    }

    private void gerenciarLogado(RegistroJornada registroJornada) {
        try {
            PontoConfiguracao pontoConfiguracao = pontoConfiguracaoRepository.findByEmpresaId(registroJornada.getUsuario().getEmpresaId()).orElseThrow(PontoConfiguracaoNaoEncontradoException::new);
            Usuario usuario = registroJornada.getUsuario();

            if (registroJornada.getStatusAtual() == null) {
                if (isInRegularTime(registroJornada) &&
                    canUsuarioLogar(registroJornada) &&
                    Duration.between(usuario.getVistoPorUltimo(), LocalDateTime.now()).toSeconds() < 60) {
                        logar(registroJornada);
                    }
            } else {
                if (LocalTime.now().isAfter(LocalTime.of(23, 55))) {
                    deslogar(registroJornada);
                } else {
                    long horasTrabalhadas = calculateHorasTrabalhadas(registroJornada);
                    long horasATrabalhar = calculateHorasATrabalhar(registroJornada);
                    if (!registroJornada.getHoraExtraPermitida()) {
                        if (horasTrabalhadas > horasATrabalhar)
                            deslogar(registroJornada);
                    } else {
                        if (horasTrabalhadas > (horasATrabalhar + pontoConfiguracao.getHoraExtraMax()))
                            deslogar(registroJornada);
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

                if (!isEmHoraExtra(registroJornada)) {
                    if (registroJornada.getStatusAtual().getJornadaStatusId().equals(pontoConfiguracao.getStatusRegularId())) {
                        if (Duration.between(usuario.getVistoPorUltimo(), agora).toSeconds() > pontoConfiguracao.getIntervaloVerificacaoRegular()) {
                            alterarStatus(registroJornada, pontoConfiguracao.getStatusAusente());
                        }
                    } else if (registroJornada.getStatusAtual().getJornadaStatusId().equals(pontoConfiguracao.getStatusAusenteId())) {
                        if (Duration.between(usuario.getVistoPorUltimo(), agora).toSeconds() <= pontoConfiguracao.getIntervaloVerificacaoRegular()) {
                            alterarStatus(registroJornada, pontoConfiguracao.getStatusRegular());
                        }
                    }
                } else {
                    if (registroJornada.getStatusAtual().getJornadaStatusId().equals(pontoConfiguracao.getStatusRegularId())) {
                        if (Duration.between(usuario.getVistoPorUltimo(), agora).toSeconds() > pontoConfiguracao.getIntervaloVerificacaoHoraExtra()) {
                            alterarStatus(registroJornada, pontoConfiguracao.getStatusAusente());
                        }
                    } else if (registroJornada.getStatusAtual().getJornadaStatusId().equals(pontoConfiguracao.getStatusAusenteId())) {
                        if (Duration.between(usuario.getVistoPorUltimo(), agora).toSeconds() <= pontoConfiguracao.getIntervaloVerificacaoHoraExtra()) {
                            alterarStatus(registroJornada, pontoConfiguracao.getStatusRegular());
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

    public void criarRegistroDeUsuariosAtivos() {
        List<Integer> usuarioIdList = usuarioRepository.findAllUsuarioIdByAtivoTrue();

        for (Integer usuarioId : usuarioIdList) {
            try {
                this.getRegistroJornadaByUsuarioIdHoje(usuarioId);
            } catch (Exception e) {

            }
        }
        
    }

}