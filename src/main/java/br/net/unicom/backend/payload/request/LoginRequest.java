package br.net.unicom.backend.payload.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class LoginRequest {

    @NotBlank
    @Pattern(regexp = "(^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$)|^\\d+$", message = "Email/matrícula inválido")
    private String login;
    
    @NotBlank
    @Length(min = 3, max = 20)
    private String senha;

    private String dominio;

}
