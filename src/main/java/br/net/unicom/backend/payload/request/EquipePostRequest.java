package br.net.unicom.backend.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class EquipePostRequest {

    private Integer supervisorId;

    private Integer gerenteId;

    @NotBlank
    private String nome;

    private String icon;

    private String iconFilename;

}