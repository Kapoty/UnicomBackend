package br.net.unicom.backend.configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.net.unicom.backend.model.FiltroVenda;
import br.net.unicom.backend.model.Venda;
import br.net.unicom.backend.model.VendaProduto;
import br.net.unicom.backend.payload.request.VendaListRequest;
import br.net.unicom.backend.payload.request.VendaPatchRequest;
import br.net.unicom.backend.payload.request.VendaPostRequest;
import br.net.unicom.backend.payload.request.VendaProdutoRequest;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.typeMap(VendaPatchRequest.class, Venda.class).addMappings(mapper -> {
            mapper.skip(Venda::setVendedorId);
            mapper.skip(Venda::setSupervisorId);
            mapper.skip(Venda::setAuditorId);
            mapper.skip(Venda::setCadastradorId);
            mapper.skip(Venda::setVendedorExterno);
            mapper.skip(Venda::setSupervisorExterno);
            mapper.skip(Venda::setAuditorExterno);
            mapper.skip(Venda::setCadastradorExterno);
            mapper.skip(Venda::setProdutoList);
            mapper.skip(Venda::setFaturaList);
        });

        modelMapper.typeMap(VendaProdutoRequest.class, VendaProduto.class).addMappings(mapper -> {
            mapper.skip(VendaProduto::setPortabilidadeList);
        });

        modelMapper.typeMap(VendaPostRequest.class, Venda.class).addMappings(mapper -> {
            mapper.skip(Venda::setProdutoList);
            mapper.skip(Venda::setFaturaList);
        });

        modelMapper.typeMap(VendaListRequest.class, FiltroVenda.class).addMappings(mapper -> {
            mapper.skip(FiltroVenda::setStatusIdList);
        });

        return modelMapper;
    }

}
