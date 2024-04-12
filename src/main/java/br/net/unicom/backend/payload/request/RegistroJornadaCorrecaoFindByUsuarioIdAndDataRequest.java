package br.net.unicom.backend.payload.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class RegistroJornadaCorrecaoFindByUsuarioIdAndDataRequest {

    @NotNull
    Integer usuarioId;

    @NotNull
    LocalDate data;

}
