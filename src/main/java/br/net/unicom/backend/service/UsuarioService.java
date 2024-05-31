package br.net.unicom.backend.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.net.unicom.backend.model.Equipe;
import br.net.unicom.backend.model.Papel;
import br.net.unicom.backend.model.RegistroPonto;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.exception.RegistroPontoFullException;
import br.net.unicom.backend.model.exception.RegistroPontoLockedException;
import br.net.unicom.backend.model.exception.RegistroPontoUnauthorizedException;
import br.net.unicom.backend.repository.EquipeRepository;
import br.net.unicom.backend.repository.JornadaRepository;
import br.net.unicom.backend.repository.PapelRepository;
import br.net.unicom.backend.repository.PermissaoRepository;
import br.net.unicom.backend.repository.RegistroPontoRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    @Autowired
    RegistroPontoRepository registroPontoRepository;

    @Autowired
    RegistroPontoService registroPontoService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PapelRepository papelRepository;

    @Autowired
    JornadaRepository jornadaRepository;

    @Autowired
    EquipeRepository equipeRepository;

    @Autowired
    PermissaoRepository permissaoRepository;

    @Autowired
    ModelMapper modelMapper;

    public Integer parseUsuarioIdString(UserDetailsImpl userDetails, String usuarioId) {
        if (usuarioId.equals("me")) {
            return userDetails.getId();
        }
        return Integer.valueOf(usuarioId);
    }

    public Boolean isUsuarioGreaterThan(Usuario usuarioPai, Usuario usuarioFilho) {
        List<Papel> papelFilhoList = getPapelFilhoList(usuarioPai);
        List<Equipe> minhaEquipeList = getMinhaEquipeListByUsuario(usuarioPai);

        if (!papelFilhoList.contains(usuarioFilho.getPapel()))
            return false;

        if (usuarioFilho.getEquipe() != null && !minhaEquipeList.contains(usuarioFilho.getEquipe()))        
            return false;

        return true;
    }

    public List<Usuario> getUsuarioListLessThanUsuario(Usuario usuario) {
        List<Papel> papelFilhoList = getPapelFilhoList(usuario);
        List<Equipe> minhaEquipeList = getMinhaEquipeListByUsuario(usuario);

        List<Usuario> usuarioLessThanList = usuarioRepository.findAllByEmpresaId(usuario.getEmpresaId());

        usuarioLessThanList.removeIf(u -> !papelFilhoList.contains(u.getPapel()));

        usuarioLessThanList.removeIf(u -> u.getEquipe() != null && !minhaEquipeList.contains(u.getEquipe()));

        return usuarioLessThanList;
    }

    public List<Papel> getPapelFilhoList(Usuario usuario) {
        return usuario.getPapel().getPapelMaiorQueList()
            .stream()
            .map(papelMaiorQue -> papelMaiorQue.getPapelFilho())
            .collect(Collectors.toList());
    }

    public List<Equipe> getMinhaEquipeListByUsuario(Usuario usuario) {

        if (getPermissaoList(usuario).contains("VER_TODAS_EQUIPES"))
            return equipeRepository.findAllByEmpresaId(usuario.getEmpresaId());

        return equipeRepository.findAllBySupervisorIdOrGerenteId(usuario.getUsuarioId(), usuario.getUsuarioId());
    }

    public Boolean isUsuarioSupervisorOf(Usuario usuarioPai, Usuario usuarioFilho) {
        return (usuarioFilho.getEquipe() != null && usuarioPai.getUsuarioId() == usuarioFilho.getEquipe().getSupervisorId()) ||
                (usuarioFilho.getEquipe() != null && usuarioPai.getUsuarioId() == usuarioFilho.getEquipe().getGerenteId()) ||
                (getPermissaoList(usuarioPai).contains("VER_TODAS_EQUIPES"));
    }

    public List<String> getPermissaoList(Usuario usuario) {
        return permissaoRepository.findAllByUsuarioId(usuario.getUsuarioId())
            .stream()
            .map(permissao -> permissao.getNome())
            .collect(Collectors.toList());
    }

    public String getUsuarioFotoPerfilFilename(Usuario usuario) {
        return "usuario/" + usuario.getUsuarioId() + "/foto_perfil.jpg";
    }

    public void registrarPontoByUsuarioId(Integer usuarioId) throws RegistroPontoFullException, RegistroPontoLockedException, RegistroPontoUnauthorizedException {
        LocalDateTime hoje = LocalDateTime.now();

        RegistroPonto registroPonto = registroPontoRepository.findByUsuarioIdAndData(usuarioId, hoje.toLocalDate()).orElseThrow(NoSuchElementException::new);

        if (registroPontoService.getLockedSeconds(registroPonto) != 0)
            throw new RegistroPontoLockedException();

        if (registroPonto.getEntrada() == null)
            registroPonto.setEntrada(hoje.toLocalTime());
        else if (registroPonto.getIntervaloInicio() == null)
            registroPonto.setIntervaloInicio(hoje.toLocalTime());
        else if (registroPonto.getIntervaloFim() == null)
            registroPonto.setIntervaloFim(hoje.toLocalTime());
        else if (registroPonto.getSaida() == null)
            registroPonto.setSaida(hoje.toLocalTime());
        else throw new RegistroPontoFullException();

        registroPontoRepository.save(registroPonto);
    }

    public Duration getDurationSinceUsuarioVistoPorUltimo(Usuario usuario) {
        return Duration.between(usuario.getVistoPorUltimo(), LocalDateTime.now());
    }

    public void ping(Usuario usuario) {
        usuario.setVistoPorUltimo(LocalDateTime.now());
        usuarioRepository.saveAndFlush(usuario);
    }

}
