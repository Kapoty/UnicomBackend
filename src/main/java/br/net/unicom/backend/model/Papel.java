package br.net.unicom.backend.model;

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
public class Papel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer papelId;

    @NotBlank
    @Length(max = 45)
    private String nome;

    @NotNull
    @Column(name = "empresa_id")
    private Integer empresaId;

    @ManyToOne
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private Empresa empresa;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "papel")
    @JsonIgnore
    @ToString.Exclude
    private List<PapelEmpresaPermissao> papelEmpresaPermissaoList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "papelPai")
    @JsonIgnore
    @ToString.Exclude
    private List<PapelMaiorQue> papelMaiorQueList;

}
