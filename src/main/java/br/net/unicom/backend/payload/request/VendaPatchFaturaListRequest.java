package br.net.unicom.backend.payload.request;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class VendaPatchFaturaListRequest {

    @NotBlank
    @Length(max = 500)
    private String relato;
    
    private List<@Valid VendaFaturaRequest> faturaList;

}