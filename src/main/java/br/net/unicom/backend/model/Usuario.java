package br.net.unicom.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public interface DefaultView {};
    public interface ExpandedView {};

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

    @NotBlank
    @Length(max = 200)
    private String nomeCompleto;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    @ToString.Exclude
    @JsonView(ExpandedView.class)
    private Empresa empresa;

    @Column(name = "cargo_id")
    private Integer cargoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id", insertable = false, updatable = false)
    @JsonView(ExpandedView.class)
    private Cargo cargo;

    @Column(name = "contrato_id")
    private Integer contratoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", insertable = false, updatable = false)
    @JsonView(ExpandedView.class)
    private Contrato contrato;

    @Column(name = "departamento_id")
    private Integer departamentoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", insertable = false, updatable = false)
    @JsonView(ExpandedView.class)
    private Departamento departamento;

    @Column(name = "equipe_id")
    private Integer equipeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id", insertable = false, updatable = false)
    @JsonView(ExpandedView.class)
    private Equipe equipe;

    @Column(name = "papel_id")
    @NotNull
    private Integer papelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "papel_id", insertable = false, updatable = false)
    @JsonView(ExpandedView.class)
    private Papel papel;

    @Column(name = "jornada_status_grupo_id")
    private Integer jornadaStatusGrupoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jornada_status_grupo_id", insertable = false, updatable = false)
    @JsonView(ExpandedView.class)
    private JornadaStatusGrupo jornadaStatusGrupo;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "usuario")
    @PrimaryKeyJoinColumn
    @JsonIgnore
    @ToString.Exclude
    private FiltroVenda filtroVenda;
}
