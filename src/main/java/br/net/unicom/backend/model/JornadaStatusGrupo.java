package br.net.unicom.backend.model;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "empresa_id", "nome" })
    })
@Getter @Setter @NoArgsConstructor @ToString
public class JornadaStatusGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer jornadaStatusGrupoId;

    @NotBlank
    @Length(max = 100)
    private String nome;

    @NotNull
    @Column(name = "empresa_id")
    private int empresaId;

    @ManyToOne
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private Empresa empresa;

}
