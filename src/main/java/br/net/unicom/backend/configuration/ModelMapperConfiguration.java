package br.net.unicom.backend.configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.payload.response.UsuarioResponse;
import br.net.unicom.backend.repository.PapelRepository;

@Configuration
public class ModelMapperConfiguration {

    @Autowired
    PapelRepository papelRepository;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.typeMap(Usuario.class, UsuarioResponse.class).addMapping((usuario) -> papelRepository.findAllByUsuarioId(usuario.getUsuarioId()), UsuarioResponse::setPapelList);
        return modelMapper;
    }

}
