package br.net.unicom.backend.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter @Setter @NoArgsConstructor @ToString @EqualsAndHashCode
public class PapelEmpresaPermissaoKey implements Serializable {

    @Embedded
    EmpresaPermissaoKey empresaPermissaoId;

    @Column(name = "papel_id")
    Integer papelId;

}
