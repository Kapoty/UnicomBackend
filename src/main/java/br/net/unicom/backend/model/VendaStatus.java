package br.net.unicom.backend.model;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.net.unicom.backend.model.enums.VendaStatusCategoriaEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class VendaStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vendaStatusId;

    @Column(name = "empresa_id", nullable = false)
    private Integer empresaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    Empresa empresa;

    @NotBlank
    @Length(max = 100)
    private String nome;

    @NotBlank
    @Length(max = 45)
    private String icon;

    @NotNull
    @Enumerated(EnumType.STRING)
    private VendaStatusCategoriaEnum categoria;

    private Integer ordem;

    @NotBlank
    @Length(max = 6)
    private String cor;

}
