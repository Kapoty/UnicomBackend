package br.net.unicom.backend.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.Venda;
import br.net.unicom.backend.model.projection.VendaResumidaProjection;
import br.net.unicom.backend.repository.UsuarioRepository;

@Service
public class VendaService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    UsuarioRepository usuarioRepository;

    public Boolean usuarioPodeVerVenda(Usuario usuario, Usuario vendedor, Usuario supervisor) {

        if (vendedor == null)
            return true;

        if (usuario.equals(vendedor))
            return true;

        if (usuario.equals(supervisor))
            return true;

        if (vendedor != null && usuarioService.isUsuarioGreaterThan(usuario, vendedor))
            return true;

        if (supervisor != null && usuarioService.isUsuarioGreaterThan(usuario, supervisor))
            return true;

        return false;
    }

    public Boolean usuarioPodeVerVenda(Usuario usuario, Venda venda) {
        return this.usuarioPodeVerVenda(usuario, venda.getVendedor(), venda.getSupervisor());
    }

    public Boolean usuarioPodeVerVenda(Usuario usuario, VendaResumidaProjection vendaResumidaProjection) {

        Usuario vendedor = usuarioRepository.findByUsuarioId(vendaResumidaProjection.getVendedorId()).orElse(null);
        Usuario supervisor = usuarioRepository.findByUsuarioId(vendaResumidaProjection.getSupervisorId()).orElse(null);

        return this.usuarioPodeVerVenda(usuario, vendedor, supervisor);
    }

}
