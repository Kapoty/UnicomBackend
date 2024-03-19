package br.net.unicom.backend.service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.net.unicom.backend.model.RegistroPonto;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.exception.RegistroPontoFullException;
import br.net.unicom.backend.model.exception.RegistroPontoLockedException;
import br.net.unicom.backend.model.exception.RegistroPontoUnauthorizedException;
import br.net.unicom.backend.repository.RegistroPontoRepository;

@Service
public class UsuarioService {

    @Autowired
    RegistroPontoRepository registroPontoRepository;

    @Autowired
    RegistroPontoService registroPontoService;

    public String getUsuarioFotoPerfilFilename(Usuario usuario) {
        return "usuario/" + usuario.getUsuarioId() + "/foto_perfil.jpg";
        //return "usuario_foto_perfil_" + usuario.getUsuarioId() + ".jpg";
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
