package br.net.unicom.backend.payload.request;

import java.time.LocalDate;
import java.time.LocalTime;

import br.net.unicom.backend.model.enums.RegistroJornadaCorrecaoObservacaoEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class RegistroJornadaCorrecaoPatchByUsuarioIdAndDataRequest {

    @NotNull
    private Integer usuarioId;

    @NotNull
    private LocalDate data;

    @NotNull
    private LocalTime jornadaEntrada;

    @NotNull
    private LocalTime jornadaIntervaloInicio;

    @NotNull
    private LocalTime jornadaIntervaloFim;

    @NotNull
    private LocalTime jornadaSaida;

    @AssertTrue(message = "os horários devem ser sucessivos")
    private boolean isJornadaOrderValid() {
        if (jornadaEntrada == null ||
        jornadaIntervaloInicio == null ||
        jornadaIntervaloFim == null ||
        jornadaSaida == null)
            return true;
        if (jornadaEntrada.compareTo(jornadaIntervaloInicio) >= 0)
            return false;
        if (jornadaIntervaloInicio.compareTo(jornadaIntervaloFim) >= 0)
            return false;
        if (jornadaIntervaloFim.compareTo(jornadaSaida) >= 0)
            return false;
        return true;
    }

    private Integer contratoId;

    private String justificativa;

    @NotNull
    private Integer horasTrabalhadas;

    private LocalTime entrada;
    
    private LocalTime saida;

    @AssertTrue(message = "os horários devem ser sucessivos")
    private boolean isEntradaSaidaOrderValid() {
        if (entrada == null ||
        saida == null)
            return true;
        if (entrada.compareTo(saida) >= 0)
            return false;
        return true;
    }

    @NotNull
    private Boolean horaExtraPermitida;

    @Enumerated(EnumType.STRING)
    private RegistroJornadaCorrecaoObservacaoEnum observacao;

    @NotNull
    private Integer ajusteHoraExtra;

    private Boolean aprovada;

}
