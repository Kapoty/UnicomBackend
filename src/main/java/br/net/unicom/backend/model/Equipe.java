package br.net.unicom.backend.model;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "empresa_id", "nome" })
    })
@Getter @Setter @NoArgsConstructor @ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer equipeId;

    @NotBlank
    @Length(max = 100)
    private String nome;

    @NotNull
    @Column(name = "empresa_id")
    private int empresaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private Empresa empresa;

    @Column(name = "supervisor_id")
    private Integer supervisorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", referencedColumnName = "usuarioId", insertable = false, updatable = false)
    @ToString.Exclude
    @JsonIgnore
    private Usuario supervisor;

    @Column(name = "gerente_id")
    private Integer gerenteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gerente_id", referencedColumnName = "usuarioId", insertable = false, updatable = false)
    @ToString.Exclude
    @JsonIgnore
    private Usuario gerente;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "equipe")
    @ToString.Exclude
    @JsonIgnore
    private List<Usuario> usuarioList;

}
