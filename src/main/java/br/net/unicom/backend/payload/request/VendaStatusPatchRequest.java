package br.net.unicom.backend.payload.request;

import org.hibernate.validator.constraints.Length;

import br.net.unicom.backend.model.enums.VendaStatusCategoriaEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class VendaStatusPatchRequest {

    @NotBlank
    @Length(max = 100)
    private String nome;

    @NotBlank
    @Length(max = 45)
    private String icon;

    @NotNull
    @Enumerated(EnumType.STRING)
    private VendaStatusCategoriaEnum categoria;

    private Integer ordem;

    @NotBlank
    @Length(max = 6)
    private String cor;

}