package br.net.unicom.backend.model;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class JornadaExcecao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer jornadaExcecaoId;

    @NotNull
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @ManyToOne
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private Usuario usuario;

    @NotNull
    private LocalDate data;

    @NotNull
    private Boolean registraPonto;

    @NotNull
    private LocalTime entrada;

    @NotNull
    private LocalTime intervaloInicio;

    @NotNull
    private LocalTime intervaloFim;

    @NotNull
    private LocalTime saida;

}
