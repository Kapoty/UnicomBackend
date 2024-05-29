package br.net.unicom.backend.payload.request;

import java.time.LocalDate;
import java.util.List;

import br.net.unicom.backend.model.FiltroVendaTipoDataEnum;
import br.net.unicom.backend.model.VendaTipoProdutoEnum;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class VendaListRequest {

    private VendaTipoProdutoEnum tipoProduto;

    @NotNull
    private String pdv;

    private LocalDate safra;

    private FiltroVendaTipoDataEnum tipoData;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    @NotNull
    private List<Integer> statusIdList;

    @NotNull
    private String os;

    @NotNull
    private String cpf;

    @NotNull
    private String nome;

    private Integer offset = 0;

    @Min(1)
    @Max(10000)
    private Integer limit = 2000;

    /*@AssertTrue(message = "o intervalo máximo é de 3 meses")
    private boolean isDataRangeValid() {
        if (ChronoUnit.DAYS.between(dataInicio, dataFim) > 93)
            return false;
        return true;
    }*/

    @AssertTrue(message = "data inválida")
    private boolean isDataValid() {
        if (dataInicio == null || dataFim == null)
            return true;
        if (dataInicio.compareTo(dataFim) > 0)
            return false;
        return true;
    }

}