package br.net.unicom.backend.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter @Setter @NoArgsConstructor @ToString @EqualsAndHashCode @AllArgsConstructor
public class UsuarioPapelKey implements Serializable {

    Integer usuarioId;

    Integer papelId;

}
