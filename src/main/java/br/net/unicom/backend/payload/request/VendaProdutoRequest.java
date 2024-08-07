package br.net.unicom.backend.payload.request;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import br.net.unicom.backend.model.enums.VendaProdutoTipoDeLinhaEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class VendaProdutoRequest {

    @NotBlank
    @Length(max = 100)
    private String nome;

    @NotNull
    @Length(max = 100)
    private String adicionais;

    @NotNull
    @Min(1)
    private Double valor;

    @NotNull
    @Min(1)
    private Double valorAcordado;

    @NotNull
    private Boolean telefoneFixo;

    @NotNull
    private Double valorTelefoneFixo;

    @NotNull
    @Length(max = 8)
    private String numeroTelefoneFixo;

    @NotNull
    private VendaProdutoTipoDeLinhaEnum tipoDeLinha;

    @NotNull
    @Length(max = 2)
    private String ddd;

    @NotNull
    @Length(max = 20)
    private String operadora;

    @NotNull
    private Integer quantidade;

    private List<@Valid VendaProdutoPortabilidadeRequest> portabilidadeList;

}
