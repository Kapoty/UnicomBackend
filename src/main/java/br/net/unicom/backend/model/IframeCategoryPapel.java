package br.net.unicom.backend.model;

import br.net.unicom.backend.model.key.IframeCategoryPapelKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class IframeCategoryPapel {

    @EmbeddedId
    IframeCategoryPapelKey iframeCategoryPapelId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("iframeCategoryId")
    @JoinColumn(name = "iframe_category_id")
    IframeCategory iframeCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("papelId")
    @JoinColumn(name = "papel_id")
    Papel papel;

}
