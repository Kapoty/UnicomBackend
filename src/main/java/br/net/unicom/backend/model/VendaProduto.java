package br.net.unicom.backend.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.net.unicom.backend.model.key.VendaProdutoKey;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VendaProduto {

    @EmbeddedId
    @EqualsAndHashCode.Include
    VendaProdutoKey vendaProdutoId;
    
    @ManyToOne
    @MapsId("vendaId")
    @JoinColumn(name = "venda_id")
    @JsonIgnore
    @ToString.Exclude
    Venda venda;

    @Enumerated(EnumType.STRING)
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "vendaProduto", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("vendaProdutoPortabilidadeId.portabilidadeId")
    private List<VendaProdutoPortabilidade> portabilidadeList = null;

    public VendaProduto(Venda venda, Integer produtoId) {
        this.setVendaProdutoId(new VendaProdutoKey(venda.getVendaId(), produtoId));
        this.setVenda(venda);
    }

    public List<VendaProdutoPortabilidade> getPortabilidadeList() {
        if (this.portabilidadeList == null)
            this.portabilidadeList = new ArrayList<>();
        return this.portabilidadeList;
    }

    public void setPortabilidadeList(List<VendaProdutoPortabilidade> portabilidadeList) {
        if (this.portabilidadeList == null) {
            this.portabilidadeList = portabilidadeList;
        } else {
            this.portabilidadeList.clear();
            this.portabilidadeList.addAll(portabilidadeList);
        }
    }

}