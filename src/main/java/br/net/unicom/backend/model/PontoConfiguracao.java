package br.net.unicom.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class PontoConfiguracao {

    @Id
    @Column(name = "empresa_id")
    private Integer empresaId;

    @NotNull
    private Integer intervaloVerificacaoRegular;

    @NotNull
    private Integer intervaloVerificacaoHoraExtra;

    @NotNull
    private Integer horaExtraMax;

    @Column(name = "status_regular_id", nullable = false)
    @NotNull
    private Integer statusRegularId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_regular_id", referencedColumnName = "jornada_status_id", insertable = false, updatable = false)
    private JornadaStatus statusRegular;

    @Column(name = "status_ausente_id", nullable = false)
    @NotNull
    private Integer statusAusenteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_ausente_id", referencedColumnName = "jornada_status_id", insertable = false, updatable = false)
    private JornadaStatus statusAusente;

    @NotNull
    private Integer diaFechamentoFolha;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "empresa_id")
    @JsonIgnore
    @ToString.Exclude
    private Empresa empresa;

    public PontoConfiguracao(Empresa empresa) {
        this.empresa = empresa;
    }

}
