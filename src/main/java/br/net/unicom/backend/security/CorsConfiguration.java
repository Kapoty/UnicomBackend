package br.net.unicom.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration {

    @Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1:8000")
				.allowedOrigins("http://127.0.0.1:8080")
				.allowedOrigins("https://www.unicom.net.br")
				.allowedOrigins("https://unisystem.unicom.net.br")
				.allowedOrigins("*")
				.allowedMethods("*");
			}
		};
	}

}
