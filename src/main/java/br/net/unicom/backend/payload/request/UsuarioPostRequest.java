package br.net.unicom.backend.payload.request;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UsuarioPostRequest {

    @Email
    @NotBlank
    @Length(max = 256)
    private String email;

    @NotBlank
    @Length(min = 3, max = 20)
    private String senha;

    @NotBlank
    @Length(max = 200)
    private String nome;

    @NotBlank
    @Length(max = 200)
    private String nomeCompleto;

    @NotNull
    private Boolean ativo;

    private Integer matricula;

    @NotNull
    private Integer papelId;

    private LocalDate dataNascimento;

    @NotBlank
    @Length(min = 11, max = 11)
    private String cpf;

    @Length(min = 11, max = 11)
    private String telefoneCelular;

    @Length(min = 11, max = 11)
    private String whatsapp;

    private LocalDate dataContratacao;

    private Integer cargoId;

    private Integer contratoId;

    private Integer departamentoId;

    private Integer equipeId;

    private Integer jornadaStatusGrupoId;

}
