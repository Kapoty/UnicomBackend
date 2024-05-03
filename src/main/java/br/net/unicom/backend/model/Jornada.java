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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Jornada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer jornadaId;

    @NotNull
    @Column(name = "empresa_id")
    private Integer empresaId;

    @ManyToOne
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private Empresa empresa;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @ManyToOne
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private Usuario usuario;

    private LocalTime entrada;

    private LocalTime intervaloInicio;

    private LocalTime intervaloFim;

    private LocalTime saida;

    @NotNull
    private Integer prioridade;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private Boolean segunda = false;

    private Boolean terca = false;

    private Boolean quarta = false;

    private Boolean quinta = false;

    private Boolean sexta = false;

    private Boolean sabado = false;

    private Boolean domingo = false;

    @NotBlank
    @Length(max = 100)
    private String nome;

}
