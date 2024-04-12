package br.net.unicom.backend.model.key;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter @Setter @NoArgsConstructor @ToString @EqualsAndHashCode
public class IframeCategoryPapelKey implements Serializable {

    Integer iframeCategoryId;

    Integer papelId;

}
