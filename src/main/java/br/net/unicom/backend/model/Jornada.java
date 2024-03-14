package br.net.unicom.backend.model;

import java.time.LocalTime;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "empresa_id", "nome", "entrada", "intervalo_inicio", "intervalo_fim", "saida"})
    })
@Getter @Setter @NoArgsConstructor @ToString
public class Jornada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer jornadaId;

    @NotNull
    private LocalTime entrada;

    @NotNull
    private LocalTime intervaloInicio;

    @NotNull
    private LocalTime intervaloFim;

    @NotNull
    private LocalTime saida;

    @NotNull
    @Column(name = "empresa_id")
    private int empresaId;

    @ManyToOne
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private Empresa empresa;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jornada")
    @JsonIgnore
    @ToString.Exclude
    private List<Usuario> usuarioList;

}
