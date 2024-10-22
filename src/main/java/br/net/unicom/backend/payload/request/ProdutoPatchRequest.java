package br.net.unicom.backend.payload.request;

import org.hibernate.validator.constraints.Length;

import br.net.unicom.backend.model.enums.VendaTipoProdutoEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class ProdutoPatchRequest {

    @NotBlank
    @Length(max = 100)
    private String nome;

    @NotNull
    @Enumerated(EnumType.STRING)
    private VendaTipoProdutoEnum tipo;

    @NotNull
    private Integer ordem;

}