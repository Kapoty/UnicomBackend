package br.net.unicom.backend.model;

import java.time.LocalDate;
import java.time.LocalTime;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    @Length(max = 50)
    private String contratoNome;

    @NotNull
    private LocalTime jornadaEntrada;

    @NotNull
    private LocalTime jornadaIntervaloInicio;

    @NotNull
    private LocalTime jornadaIntervaloFim;

    @NotNull
    private LocalTime jornadaSaida;

    @NotNull
    private Boolean horaExtraAuto;

    @NotNull
    @Column(name = "status_id")
    private Integer statusId;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "registro_jornada_status_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private RegistroJornadaStatus status;

    @NotNull
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @ManyToOne
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private Usuario usuario;

}
