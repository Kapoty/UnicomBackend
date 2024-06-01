package br.net.unicom.backend.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.net.unicom.backend.model.enums.VendaFaturaStatusEnum;
import br.net.unicom.backend.model.key.VendaFaturaKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class VendaFatura {

    @EmbeddedId
    @EqualsAndHashCode.Include
    VendaFaturaKey vendaFaturaId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vendaId")
    @JoinColumn(name = "venda_id")
    @JsonIgnore
    @ToString.Exclude
    Venda venda;

    @NotNull
    private LocalDate mes;

    @Enumerated(EnumType.STRING)
    @NotNull
    private VendaFaturaStatusEnum status;

    @NotNull
    private Double valor;

    public VendaFatura(Venda venda, Integer faturaId) {
        this.setVendaFaturaId(new VendaFaturaKey(venda.getVendaId(), faturaId));
        this.setVenda(venda);
    }

}
