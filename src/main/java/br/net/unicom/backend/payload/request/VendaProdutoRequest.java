package br.net.unicom.backend.payload.request;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import br.net.unicom.backend.model.VendaTipoProdutoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class VendaProdutoRequest {
    private VendaTipoProdutoEnum tipo;

    @NotBlank
    @Length(max = 100)
    private String nome;

    @NotNull
    private Double valor;

    @NotNull
    private Integer quantidade;

    @NotNull
    private Boolean telefoneFixo;

    @NotNull
    private Double valorTelefoneFixo;

    @NotNull
    private Boolean portabilidade;

    private List<VendaProdutoPortabilidadeRequest> portabilidadeList;

}
