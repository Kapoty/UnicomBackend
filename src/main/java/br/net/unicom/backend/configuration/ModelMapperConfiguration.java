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
            mapper.skip(Venda::setAgenteBiometriaId);
            mapper.skip(Venda::setVendedorExterno);
            mapper.skip(Venda::setSupervisorExterno);
            mapper.skip(Venda::setAuditorExterno);
            mapper.skip(Venda::setCadastradorExterno);
            mapper.skip(Venda::setAgenteBiometriaExterno);
            mapper.skip(Venda::setProdutoList);
            mapper.skip(Venda::setFaturaList);

            mapper.skip(Venda::setOs);
            mapper.skip(Venda::setCustcode);
            mapper.skip(Venda::setOrdem);
            mapper.skip(Venda::setReimputado);
            mapper.skip(Venda::setDataInstalacao);
            mapper.skip(Venda::setVendaOriginal);
            mapper.skip(Venda::setBrscan);
            mapper.skip(Venda::setSuporte);
            mapper.skip(Venda::setLoginVendedor);
        });

        modelMapper.typeMap(VendaProdutoRequest.class, VendaProduto.class).addMappings(mapper -> {
            mapper.skip(VendaProduto::setPortabilidadeList);
        });

        modelMapper.typeMap(VendaPostRequest.class, Venda.class).addMappings(mapper -> {
            mapper.skip(Venda::setVendedorId);
            mapper.skip(Venda::setSupervisorId);
            mapper.skip(Venda::setAuditorId);
            mapper.skip(Venda::setCadastradorId);
            mapper.skip(Venda::setAgenteBiometriaId);
            mapper.skip(Venda::setVendedorExterno);
            mapper.skip(Venda::setSupervisorExterno);
            mapper.skip(Venda::setAuditorExterno);
            mapper.skip(Venda::setCadastradorExterno);
            mapper.skip(Venda::setAgenteBiometriaExterno);
            mapper.skip(Venda::setProdutoList);
            mapper.skip(Venda::setFaturaList);

            mapper.skip(Venda::setOs);
            mapper.skip(Venda::setCustcode);
            mapper.skip(Venda::setOrdem);
            mapper.skip(Venda::setReimputado);
            mapper.skip(Venda::setDataInstalacao);
            mapper.skip(Venda::setVendaOriginal);
            mapper.skip(Venda::setBrscan);
            mapper.skip(Venda::setSuporte);
            mapper.skip(Venda::setLoginVendedor);
        });

        modelMapper.typeMap(VendaListRequest.class, FiltroVenda.class).addMappings(mapper -> {
            mapper.skip(FiltroVenda::setStatusIdList);
        });

        return modelMapper;
    }

}
