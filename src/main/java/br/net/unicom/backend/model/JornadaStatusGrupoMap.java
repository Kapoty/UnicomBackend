package br.net.unicom.backend.model;

import br.net.unicom.backend.model.key.JornadaStatusGrupoMapKey;
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
public class JornadaStatusGrupoMap {

    @EmbeddedId
    JornadaStatusGrupoMapKey jornadaStatusGrupoMapKey;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("jornadaStatusId")
    @JoinColumn(name = "jornada_status_id")
    JornadaStatus jornadaStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("jornadaStatusGrupoId")
    @JoinColumn(name = "jornada_status_grupo_id")
    JornadaStatusGrupo jornadaStatusGrupo;

}
