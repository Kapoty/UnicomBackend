package br.net.unicom.backend.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import br.net.unicom.backend.model.enums.VendaProdutoTipoDeLinhaEnum;
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

    public interface DefaultView {};
    public interface WithPortabilidadeListView {};

    @EmbeddedId
    @EqualsAndHashCode.Include
    VendaProdutoKey vendaProdutoId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vendaId")
    @JoinColumn(name = "venda_id")
    @JsonIgnore
    @ToString.Exclude
    Venda venda;

    @NotBlank
    @Length(max = 100)
    private String nome;

    @NotNull
    @Length(max = 100)
    private String adicionais;

    @NotNull
    private Double valor;

    @NotNull
    private Boolean telefoneFixo;

    @NotNull
    private Double valorTelefoneFixo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private VendaProdutoTipoDeLinhaEnum tipoDeLinha;

    @NotNull
    @Length(max = 2)
    private String ddd;

    @NotNull
    @Length(max = 20)
    private String operadora;

    @NotNull
    private Integer quantidade;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vendaProduto", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("vendaProdutoPortabilidadeId.portabilidadeId")
    @JsonView(WithPortabilidadeListView.class)
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
