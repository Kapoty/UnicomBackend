package br.net.unicom.backend.model;

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
@Getter @Setter @NoArgsConstructor @ToString
public class RegistroJornadaStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registro_jornada_status_id")
    private Integer registroJornadaStatusId;
    
    @Column(name = "registro_jornada_id", nullable = false)
    private Integer registroJornadaId;

    @ManyToOne
    @JoinColumn(name = "registro_jornada_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    RegistroJornada registroJornada;

    @Column(name = "jornada_status_id", nullable = false)
    private Integer jornadaStatusId;

    @ManyToOne
    @JoinColumn(name = "jornada_status_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    RegistroJornada jornadaStatus;

    @NotNull
    private LocalTime inicio;

    private LocalTime fim;
}
