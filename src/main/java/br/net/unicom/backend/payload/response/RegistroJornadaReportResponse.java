package br.net.unicom.backend.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class RegistroJornadaReportResponse {

    private Integer mes;
    private Integer ano;
    private RegistroJornadaReportUsuarioResponse usuario;
    private List<RegistroJornadaReportDayResponse> dayList;

}