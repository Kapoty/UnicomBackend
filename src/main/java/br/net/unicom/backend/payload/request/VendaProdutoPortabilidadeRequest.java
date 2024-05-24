package br.net.unicom.backend.payload.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class VendaProdutoPortabilidadeRequest {

    @NotBlank
    @Length(max = 9)
    private String telefone;

}
