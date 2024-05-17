package br.net.unicom.backend.model.key;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter @Setter @NoArgsConstructor @ToString @EqualsAndHashCode @AllArgsConstructor
public class VendaProdutoKey implements Serializable {

    @Column(name = "venda_id")
    Integer vendaId;

    @Column(name = "produto_id")
    Integer produtoId;

}
