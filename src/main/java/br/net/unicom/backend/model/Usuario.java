package br.net.unicom.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "empresa_id", "matricula" })
    })
@Getter @Setter @NoArgsConstructor @ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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

    @Column(insertable = false)
    private LocalDateTime vistoPorUltimo;

    @NotNull
    @Column(name = "empresa_id")
    private Integer empresaId;

    @ManyToOne
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private Empresa empresa;

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

    @Column(name = "equipe_id")
    private Integer equipeId;

    @ManyToOne
    @JoinColumn(name = "equipe_id", insertable = false, updatable = false)
    private Equipe equipe;

    @Column(name = "papel_id")
    @NotNull
    private Integer papelId;

    @ManyToOne
    @JoinColumn(name = "papel_id", insertable = false, updatable = false)
    private Papel papel;

    @Column(name = "jornada_status_grupo_id")
    private Integer jornadaStatusGrupoId;

    @ManyToOne
    @JoinColumn(name = "jornada_status_grupo_id", insertable = false, updatable = false)
    private JornadaStatusGrupo jornadaStatusGrupo;

}
