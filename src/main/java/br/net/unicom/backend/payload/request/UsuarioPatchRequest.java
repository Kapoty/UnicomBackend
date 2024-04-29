package br.net.unicom.backend.payload.request;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UsuarioPatchRequest {

    private Optional<@Email @NotBlank @Length(max = 256) String> email;

    private Optional<@NotBlank @Length(min = 3, max = 20) String> senha;

    private Optional<@NotBlank @Length(max = 200) String> nome;

    private Optional<@NotNull Boolean> ativo;

    private Optional<Integer> matricula;

    private Optional<LocalDate> dataNascimento;

    private Optional<@Length(min = 11, max = 11) String> cpf;

    private Optional<@Length(min = 11, max = 11) String> telefoneCelular;

    private Optional<@Length(min = 11, max = 11) String> whatsapp;

    private Optional<@Past LocalDate> dataContratacao;

    private Optional<Integer> cargoId;

    private Optional<Integer> contratoId;

    private Optional<Integer> departamentoId;

    private Optional<Integer> equipeId;

    private Optional<Integer> papelId;

    private Optional<@Valid JornadaRequest> jornada;

}
