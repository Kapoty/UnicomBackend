package br.net.unicom.backend.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class RegistroJornadaAlterarStatusRequest {

    @NotBlank
    private String token;

    @NotNull
    private Integer jornadaStatusId;

}
