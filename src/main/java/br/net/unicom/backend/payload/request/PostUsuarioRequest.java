package br.net.unicom.backend.payload.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class PostUsuarioRequest {

    @Email
    @NotBlank
    @Length(max = 256)
    private String email;

    @NotBlank
    @Length(min = 3, max = 12)
    private String senha;

    @NotBlank
    @Length(max = 200)
    private String nome;

    @NotNull
    private Boolean ativo;

    private Integer matricula;

}
