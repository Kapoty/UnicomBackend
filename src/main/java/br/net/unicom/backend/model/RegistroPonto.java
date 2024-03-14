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
public class RegistroPonto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer registroPontoId;

    @NotNull
    private LocalDate data;

    private LocalTime entrada;

    private LocalTime intervaloInicio;

    private LocalTime intervaloFim;

    private LocalTime saida;

    @NotNull
    private LocalTime jornadaEntrada;

    @NotNull
    private LocalTime jornadaIntervaloInicio;

    @NotNull
    private LocalTime jornadaIntervaloFim;

    @NotNull
    private LocalTime jornadaSaida;

    @NotNull
    @Length(max = 100)
    private String contratoNome;

    @NotNull
    @Column(name = "usuario_id")
    private int usuarioId;

    @ManyToOne
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private Usuario usuario;

}
