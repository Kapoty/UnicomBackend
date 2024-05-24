package br.net.unicom.backend.model;

import java.time.LocalDateTime;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class VendaAtualizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vendaAtualizacaoId;

    @Column(name = "venda_id", nullable = false)
    private Integer vendaId;

    @ManyToOne
    @JoinColumn(name = "venda_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    Venda venda;

    @Column(name = "status_id", nullable = false)
    private Integer statusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    VendaStatus status;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    Usuario usuario;

    @NotNull
    private LocalDateTime data;

    @NotNull
    @Length(max = 200)
    private String relato;

}
