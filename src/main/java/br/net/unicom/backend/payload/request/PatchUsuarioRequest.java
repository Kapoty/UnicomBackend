package br.net.unicom.backend.payload.request;

import java.util.Optional;

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
public class PatchUsuarioRequest {

    private Optional<@Email @NotBlank @Length(max = 256) String> email;

    private Optional<@NotBlank @Length(min = 3, max = 12) String> senha;

    private Optional<@NotBlank @Length(max = 200) String> nome;

    private Optional<@NotNull Boolean> ativo;

    private Optional<Integer> matricula;

}
