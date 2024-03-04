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
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Papel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer papelId;

    @NotNull
    @Length(max = 45)
    private String nome;

    @NotNull
    @Column(name = "empresa_id")
    private int empresaId;

    @ManyToOne
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private Empresa empresa;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "papel")
    @JsonIgnore
    @ToString.Exclude
    private List<PapelEmpresaPermissao> papelEmpresaPermissoes;

}
