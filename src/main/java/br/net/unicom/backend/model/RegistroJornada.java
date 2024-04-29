package br.net.unicom.backend.model;

import java.time.LocalDate;
import java.time.LocalTime;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "usuario_id", "date" })
    })
@Getter @Setter @NoArgsConstructor @ToString
public class RegistroJornada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer registroJornadaId;

    @NotNull
    private LocalDate data;

    @NotNull
    private LocalTime jornadaEntrada;

    @NotNull
    private LocalTime jornadaIntervaloInicio;

    @NotNull
    private LocalTime jornadaIntervaloFim;

    @NotNull
    private LocalTime jornadaSaida;

    @NotNull
    private Boolean horaExtraPermitida;

    @Column(name = "status_atual_id")
    private Integer statusAtualId;

    @ManyToOne
    @JoinColumn(name = "status_atual_id", referencedColumnName = "registro_jornada_status_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private RegistroJornadaStatus statusAtual;

    @NotNull
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @ManyToOne
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private Usuario usuario;

    @NotNull
    @Column(name = "contrato_id")
    private Integer contratoId;

    @ManyToOne
    @JoinColumn(name = "contrato_id", insertable = false, updatable = false)
    private Contrato contrato;
}
