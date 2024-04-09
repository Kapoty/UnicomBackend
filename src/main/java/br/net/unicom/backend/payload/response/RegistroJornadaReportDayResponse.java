package br.net.unicom.backend.payload.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import br.net.unicom.backend.model.RegistroJornada;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class RegistroJornadaReportDayResponse {

    private LocalDate data;
    private RegistroJornada registroJornada;
    private List<JornadaStatusGroupedResponse> statusGroupedList;
    private Integer horasATrabalhar;
    private Integer horasTrabalhadas;
    private Integer horasNaoTrabalhadas;
    private LocalTime entrada;
    private LocalTime saida;

}