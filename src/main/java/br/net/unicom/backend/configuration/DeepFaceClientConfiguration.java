package br.net.unicom.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration

public class DeepFaceClientConfiguration {

    @Bean
    public WebClient deepFaceClient() {
        return WebClient.create("http://localhost:5000/");
    }

}