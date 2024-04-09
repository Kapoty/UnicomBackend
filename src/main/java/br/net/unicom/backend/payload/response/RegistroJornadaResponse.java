package br.net.unicom.backend.payload.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class RegistroJornadaResponse {

    private Boolean completo;

    private Integer registroJornadaId;

    private LocalDate data;

    private String contratoNome;

    private LocalTime jornadaEntrada;

    private LocalTime jornadaIntervaloInicio;

    private LocalTime jornadaIntervaloFim;

    private LocalTime jornadaSaida;

    private Boolean horaExtraAuto;

    private Boolean emHoraExtra;

    private Boolean horaExtraPermitida;

    private RegistroJornadaStatusAtualResponse statusAtual;

    private Integer secondsToAusente;

    private Optional<List<JornadaStatusOptionResponse>> statusOptionList;

    private Optional<List<JornadaStatusGroupedResponse>> statusGroupedList;

    private Optional<Boolean> canUsuarioLogar;

    private Optional<Boolean> canSupervisorLogar;

    private Optional<Boolean> canUsuarioIniciarHoraExtra;

    private Optional<Boolean> canUsuarioDeslogar;

    private Optional<Integer> statusRegularId;

    private Optional<Integer> statusHoraExtraId;

    private Optional<Integer> statusAusenteId;

}