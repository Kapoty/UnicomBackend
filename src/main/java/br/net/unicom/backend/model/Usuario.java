package br.net.unicom.backend.model;

import java.time.LocalDate;
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
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    @Length(max = 256)
    private String email;

    @NotBlank
    @Length(max = 120)
    @JsonIgnore
    private String senha;

    @NotBlank
    @Length(max = 200)
    private String nome;

    @NotNull
    private Boolean ativo;

    private Integer matricula;

    @NotNull
    private Boolean fotoPerfil = false;

    @NotNull
    private Integer fotoPerfilVersao = 0;

    private LocalDate dataNascimento;

    @Length(min = 11, max = 11)
    private String cpf;

    @Length(min = 11, max = 11)
    private String telefoneCelular;

    @Length(min = 11, max = 11)
    private String whatsapp;

    private LocalDate dataContratacao;

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
    private List<UsuarioPapel> usuarioPapelList;

    @Column(name = "cargo_id")
    private Integer cargoId;

    @ManyToOne
    @JoinColumn(name = "cargo_id", insertable = false, updatable = false)
    private Cargo cargo;

    @Column(name = "contrato_id")
    private Integer contratoId;

    @ManyToOne
    @JoinColumn(name = "contrato_id", insertable = false, updatable = false)
    private Contrato contrato;

    @Column(name = "departamento_id")
    private Integer departamentoId;

    @ManyToOne
    @JoinColumn(name = "departamento_id", insertable = false, updatable = false)
    private Departamento departamento;

    @Column(name = "jornada_id")
    private Integer jornadaId;

    @ManyToOne
    @JoinColumn(name = "jornada_id", insertable = false, updatable = false)
    private Jornada jornada;

}
