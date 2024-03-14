package br.net.unicom.backend.service;

import org.springframework.stereotype.Service;

import br.net.unicom.backend.model.Usuario;

@Service
public class UsuarioService {

    public String getUsuarioFotoPerfilFilename(Usuario usuario) {
        return "usuario_foto_perfil_" + usuario.getUsuarioId() + ".jpg";
    }

}
