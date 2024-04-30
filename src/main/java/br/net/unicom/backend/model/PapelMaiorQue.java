package br.net.unicom.backend.model;

import br.net.unicom.backend.model.key.PapelMaiorQueKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class PapelMaiorQue {

    @EmbeddedId
    PapelMaiorQueKey papelMaiorQueKey;
    
    @ManyToOne
    @MapsId("papelPaiId")
    @JoinColumn(name = "papel_pai_id")
    Papel papelPai;

    @ManyToOne
    @MapsId("papelFilhoId")
    @JoinColumn(name = "papel_filho_id")
    Papel papelFilho;

}
