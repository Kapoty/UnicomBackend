package br.net.unicom.backend.payload.request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class PatchJornadaExcecaoByUsuarioIdAndDataRequest {

    @NotNull
    private Integer usuarioId;

    @NotNull
    private LocalDate data;

    @NotNull
    private Boolean registraPonto;

    @NotNull
    private LocalTime entrada;

    @NotNull
    private LocalTime intervaloInicio;

    @NotNull
    private LocalTime intervaloFim;

    @NotNull
    private LocalTime saida;

    @AssertTrue(message = "os horários devem ser sucessivos")
    private boolean isJornadaOrderValid() {
        if (entrada == null ||
            intervaloInicio == null ||
            intervaloFim == null ||
            saida == null)
            return true;
        if (entrada.compareTo(intervaloInicio) >= 0)
            return false;
        if (intervaloInicio.compareTo(intervaloFim) >= 0)
            return false;
        if (intervaloFim.compareTo(saida) >= 0)
            return false;
        return true;
    }

}
