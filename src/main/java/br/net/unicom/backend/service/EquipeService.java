package br.net.unicom.backend.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.net.unicom.backend.model.Equipe;
import br.net.unicom.backend.payload.response.EquipeResponse;

@Service
public class EquipeService {

    ModelMapper modelMapper;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        //this.modelMapper.typeMap(Usuario.class, UsuarioResponse.class)
        //    .addMapping(usuario -> papelRepository.findAllByUsuarioId(usuario.getUsuarioId()), UsuarioResponse::setPapelList);
    }

    public EquipeResponse equipeToEquipeResponse(Equipe equipe) {
        EquipeResponse equipeResponse = new EquipeResponse();
        this.modelMapper.map(equipe, equipeResponse);
        if (equipe.getSupervisor() != null)
            equipeResponse.setSupervisor(usuarioService.usuarioToUsuarioEquipeResponse(equipe.getSupervisor()));
        return equipeResponse;
    }

}
