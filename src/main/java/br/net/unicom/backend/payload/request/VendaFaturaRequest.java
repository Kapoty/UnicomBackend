package br.net.unicom.backend.payload.request;

import java.time.LocalDate;

import br.net.unicom.backend.model.enums.VendaFaturaStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class VendaFaturaRequest {

    @NotNull
    private LocalDate mes;

    @NotNull
    private VendaFaturaStatusEnum status;

    @NotNull
    private Double valor;

}
