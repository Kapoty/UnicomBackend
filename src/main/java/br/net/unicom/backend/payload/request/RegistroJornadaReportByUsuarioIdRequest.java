package br.net.unicom.backend.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class RegistroJornadaReportByUsuarioIdRequest {

    @NotNull
    Integer mes;

    @NotNull
    Integer ano;
}
