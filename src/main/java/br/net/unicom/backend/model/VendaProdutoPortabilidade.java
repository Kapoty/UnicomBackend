package br.net.unicom.backend.model;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.net.unicom.backend.model.key.VendaProdutoKey;
import br.net.unicom.backend.model.key.VendaProdutoPortabilidadeKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VendaProdutoPortabilidade {

    @EmbeddedId
    @EqualsAndHashCode.Include
    VendaProdutoPortabilidadeKey vendaProdutoPortabilidadeId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vendaProdutoId")
    @JoinColumns({
        @JoinColumn(name="venda_id", referencedColumnName = "venda_id"),
        @JoinColumn(name="produto_id", referencedColumnName = "produto_id")
    })
    @JsonIgnore
    @ToString.Exclude
    VendaProduto vendaProduto;

    @NotBlank
    @Length(min = 9, max = 9)
    private String telefone;

    public VendaProdutoPortabilidade(VendaProduto vendaProduto, Integer portabilidadeId) {
        this.setVendaProdutoPortabilidadeId(new VendaProdutoPortabilidadeKey(vendaProduto.getVendaProdutoId(), portabilidadeId));
        this.setVendaProduto(vendaProduto);
    }
}
