package br.net.unicom.backend.service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.net.unicom.backend.model.RegistroPonto;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.exception.RegistroPontoFullException;
import br.net.unicom.backend.model.exception.RegistroPontoLockedException;
import br.net.unicom.backend.model.exception.RegistroPontoUnauthorizedException;
import br.net.unicom.backend.payload.response.UsuarioResponse;
import br.net.unicom.backend.repository.JornadaRepository;
import br.net.unicom.backend.repository.PapelRepository;
import br.net.unicom.backend.repository.RegistroPontoRepository;
import br.net.unicom.backend.repository.UsuarioRepository;

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

    ModelMapper modelMapper;

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        //this.modelMapper.typeMap(Usuario.class, UsuarioResponse.class)
        //    .addMapping(usuario -> papelRepository.findAllByUsuarioId(usuario.getUsuarioId()), UsuarioResponse::setPapelList);
    }

    public UsuarioResponse usuarioToUsuarioResponse(Usuario usuario) {
        UsuarioResponse usuarioResponse = new UsuarioResponse();
        this.modelMapper.map(usuario, usuarioResponse);
        usuarioResponse.setPapelList(papelRepository.findAllByUsuarioId(usuario.getUsuarioId()));
        return usuarioResponse;
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

}
