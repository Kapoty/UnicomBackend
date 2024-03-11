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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer empresaId;

    @NotBlank
    @Length(max = 100)
    private String nome;

    @NotBlank
    @Length(max = 18)
    private String cnpj;

    @NotNull
    @Column(name = "grupo_id")
    private int grupoId;

    @ManyToOne
    @JoinColumn(name = "grupo_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private Grupo grupo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "empresa")
    @JsonIgnore
    @ToString.Exclude
    private List<EmpresaPermissao> empresaPermissaoList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "empresa")
    @JsonIgnore
    @ToString.Exclude
    private List<Papel> papelList;

}
