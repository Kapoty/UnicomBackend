package br.net.unicom.backend.model;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Jornada {

    @Id
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @NotNull
    private LocalTime entrada;

    @NotNull
    private LocalTime intervaloInicio;

    @NotNull
    private LocalTime intervaloFim;

    @NotNull
    private LocalTime saida;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    @ToString.Exclude
    private Usuario usuario;

    public Jornada(Usuario usuario, LocalTime entrada, LocalTime intervaloInicio, LocalTime intervaloFim, LocalTime saida) {
        this.usuario = usuario;
        this.entrada = entrada;
        this.intervaloInicio = intervaloInicio;
        this.intervaloFim = intervaloFim;
        this.saida = saida;
    }

}
