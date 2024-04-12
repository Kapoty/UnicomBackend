package br.net.unicom.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.net.unicom.backend.model.key.UsuarioPapelKey;
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
public class UsuarioPapel {

    @EmbeddedId
    UsuarioPapelKey usuarioPapelId;
    
    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    @ToString.Exclude
    Usuario usuario;

    @ManyToOne
    @MapsId("papelId")
    @JoinColumn(name = "papel_id")
    @JsonIgnore
    @ToString.Exclude
    Papel papel;

    public UsuarioPapel(Usuario usuario, Papel papel) {
        this.usuario = usuario;
        this.papel = papel;
        this.usuarioPapelId = new UsuarioPapelKey(usuario.getUsuarioId(), papel.getPapelId());
    }

}
