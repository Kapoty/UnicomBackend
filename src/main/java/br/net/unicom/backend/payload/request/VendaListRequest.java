package br.net.unicom.backend.payload.request;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class VendaListRequest {

    @NotNull
    private VendaListRequestTipoDataEnum tipoData;

    @NotNull
    private LocalDate dataInicio;

    @NotNull
    private LocalDate dataFim;

    @NotNull
    private List<Integer> statusIdList;

    @AssertTrue(message = "o intervalo máximo é de 3 meses")
    private boolean isDataRangeValid() {
        if (ChronoUnit.DAYS.between(dataInicio, dataFim) > 93)
            return false;
        return true;
    }

    @AssertTrue(message = "data inválida")
    private boolean isDataValid() {
        if (dataInicio.compareTo(dataFim) > 0)
            return false;
        return true;
    }

}