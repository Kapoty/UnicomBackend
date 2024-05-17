package br.net.unicom.backend.model.key;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter @Setter @NoArgsConstructor @ToString @EqualsAndHashCode @AllArgsConstructor
public class VendaProdutoPortabilidadeKey implements Serializable {

    @Embedded
    VendaProdutoKey vendaProdutoId;

    @Column(name = "portabilidade_id")
    Integer portabilidadeId;

}
