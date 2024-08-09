package br.net.unicom.backend.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.net.unicom.backend.model.key.VendaSuporteKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString @EqualsAndHashCode
public class VendaSuporte {

    @EmbeddedId
    @EqualsAndHashCode.Include
    VendaSuporteKey vendaSuporteId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vendaId")
    @JoinColumn(name = "venda_id")
    @JsonIgnore
    @ToString.Exclude
    Venda venda;

    @NotNull
    private LocalDate mes;

    @NotNull
    private String observacao;

    public VendaSuporte(Venda venda, Integer suporteId) {
        this.setVendaSuporteId(new VendaSuporteKey(venda.getVendaId(), suporteId));
        this.setVenda(venda);
    }

}
