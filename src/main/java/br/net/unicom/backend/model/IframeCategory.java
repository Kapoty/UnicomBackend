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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "empresa_id", "uri" })
    })
public class IframeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer iframeCategoryId;

    @NotBlank
    @Length(max = 100)
    private String titulo;

    @NotBlank
    @Length(max = 100)
    private String uri;

    @NotBlank
    @Length(max = 45)
    private String icon;

    @NotNull
    private Boolean ativo;

    @Column(name = "empresa_id")
    private Integer empresaId;

    @ManyToOne
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    Empresa empresa;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "iframeCategory")
    @JsonIgnore
    @ToString.Exclude
    private List<Iframe> iframeList;

}
