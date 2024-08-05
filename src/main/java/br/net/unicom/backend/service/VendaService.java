package br.net.unicom.backend.service;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.Venda;
import br.net.unicom.backend.model.VendaAtualizacao;
import br.net.unicom.backend.model.enums.VendaReimputadoEnum;
import br.net.unicom.backend.model.projection.VendaAtoresProjection;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.repository.VendaRepository;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;

@Service
public class VendaService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    VendaRepository vendaRepository;

    public Boolean usuarioPodeVerVenda(Usuario usuario, Usuario vendedor, Usuario supervisor) {

        //if (vendedor == null)
        //    return true;

        if (usuario.equals(vendedor))
            return true;

        if (usuario.equals(supervisor))
            return true;

        /*if (vendedor != null && usuarioService.isUsuarioGreaterThan(usuario, vendedor))
            return true;

        if (supervisor != null && usuarioService.isUsuarioGreaterThan(usuario, supervisor))
            return true;*/

        return false;
    }

    public Boolean usuarioPodeVerVenda(Usuario usuario, Venda venda) {
        return this.usuarioPodeVerVenda(usuario, venda.getVendedor(), venda.getSupervisor());
    }

    public Boolean usuarioPodeVerVenda(Usuario usuario, VendaAtoresProjection vendaAtoresProjection) {

        Usuario vendedor = usuarioRepository.findByUsuarioId(vendaAtoresProjection.getVendedorId()).orElse(null);
        Usuario supervisor = usuarioRepository.findByUsuarioId(vendaAtoresProjection.getSupervisorId()).orElse(null);

        return this.usuarioPodeVerVenda(usuario, vendedor, supervisor);
    }
    
    @Transactional
    public void novaAtualizacao(Usuario usuario, Venda venda, String relato) {

        novaAtualizacao(usuario, venda, relato, null);

    }

    @Transactional
    public void novaAtualizacao(Usuario usuario, Venda venda, String relato, String detalhes) {

        LocalDateTime dataStatus = LocalDateTime.now();

        venda.setDataStatus(dataStatus);

        VendaAtualizacao atualizacao = new VendaAtualizacao();
        atualizacao.setVendaId(venda.getVendaId());
        atualizacao.setStatusId(venda.getStatusId());
        atualizacao.setUsuarioId(usuario.getUsuarioId());
        atualizacao.setData(dataStatus);
        atualizacao.setRelato(relato);
        atualizacao.setDetalhes(detalhes);

        venda.getAtualizacaoList().add(atualizacao);

        vendaRepository.saveAndFlush(venda);

    }

    

}
