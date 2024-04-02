package br.net.unicom.backend.payload.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class RegistroJornadaResponse {

    private Integer registroJornadaId;

    private LocalDate data;

    private String contratoNome;

    private LocalTime jornadaEntrada;

    private LocalTime jornadaIntervaloInicio;

    private LocalTime jornadaIntervaloFim;

    private LocalTime jornadaSaida;

    private Boolean horaExtraAuto;

    private RegistroJornadaStatusAtualResponse statusAtual;

    private List<JornadaStatusOptionResponse> statusOptionList;

    private List<JornadaStatusGroupedResponse> statusGroupedList;

    private LocalDateTime vistoPorUltimo;

    private Boolean canUsuarioLogar;

    private Boolean canUsuarioIniciarHoraExtra;

    private Boolean canUsuarioDeslogar;

    private Boolean emHoraExtra;

    private Integer secondsToAusente;

    private Integer statusRegularId;

    private Integer statusHoraExtraId;

}