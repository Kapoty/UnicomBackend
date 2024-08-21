package br.net.unicom.backend.payload.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class VendaFindByOsOrCustcodeOrOrdemRequest {

    @NotNull
    @Length(max = 50)
    private String os;

    @NotNull
    @Length(max = 50)
    private String custcode;

    @NotNull
    @Length(max = 50)
    private String ordem;

}
