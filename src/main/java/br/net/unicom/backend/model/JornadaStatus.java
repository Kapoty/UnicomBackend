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
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class JornadaStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jornada_status_id")
    private Integer jornadaStatusId;
    
    @Column(name = "empresa_id", nullable = false)
    private Integer empresaId;

    @ManyToOne
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    Empresa empresa;

    @Column(name = "contrato_id", nullable = true)
    private Integer contratoId;

    @ManyToOne
    @JoinColumn(name = "contrato_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    Contrato contrato;

    @NotNull
    @Length(max = 50)
    private String nome;

    private Integer maxDuracao;

    private Integer maxUso;

    @NotNull
    private Boolean usuarioPodeAtivar;

    @NotNull
    private Boolean supervisorPodeAtivar;

    @NotNull
    private Boolean ativo;

    private String cor;

    @NotNull
    private Boolean agrupar;

    @NotNull
    private Boolean horaTrabalhada;
}
