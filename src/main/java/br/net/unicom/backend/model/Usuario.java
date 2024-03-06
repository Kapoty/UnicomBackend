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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "empresa_id", "matricula" })
    })
@Getter @Setter @NoArgsConstructor @ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer usuarioId;

    @Email
    @NotNull
    @Length(max = 256)
    private String email;

    @NotNull
    @Length(max = 120)
    @JsonIgnore
    private String senha;

    @NotNull
    @Length(max = 200)
    private String nome;

    @NotNull
    private Boolean ativo;

    private Integer matricula;

    @NotNull
    @Column(name = "empresa_id")
    private Integer empresaId;

    @ManyToOne
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private Empresa empresa;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "usuario")
    @JsonIgnore
    @ToString.Exclude
    private List<UsuarioPapel> usuarioPapeis;

}
