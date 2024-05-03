package br.net.unicom.backend.payload.request;

import java.time.LocalDate;
import java.time.LocalTime;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class JornadaPatchRequest {

    @NotBlank
    @Length(max = 100)
    private String nome;

    private LocalTime entrada;

    private LocalTime intervaloInicio;

    private LocalTime intervaloFim;

    private LocalTime saida;

    @AssertTrue(message = "os horários devem ser sucessivos")
    private boolean isJornadaOrderValid() {
        if (entrada == null &&
            intervaloInicio == null &&
            intervaloFim == null &&
            saida == null)
            return true;
        if (entrada == null ||
            intervaloInicio == null ||
            intervaloFim == null ||
            saida == null)
            return false;
        if (entrada.compareTo(intervaloInicio) >= 0)
            return false;
        if (intervaloInicio.compareTo(intervaloFim) >= 0)
            return false;
        if (intervaloFim.compareTo(saida) >= 0)
            return false;
        return true;
    }

    @NotNull
    private Integer prioridade;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private Boolean segunda;

    private Boolean terca;

    private Boolean quarta;

    private Boolean quinta;

    private Boolean sexta;

    private Boolean sabado;

    private Boolean domingo;

}
