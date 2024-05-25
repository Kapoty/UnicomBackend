package br.net.unicom.backend.model;

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
    @UniqueConstraint(columnNames = { "iframe_category_id", "uri" })
    })
public class Iframe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer iframeId;

    @NotBlank
    @Length(max = 100)
    private String titulo;

    @NotBlank
    @Length(max = 100)
    private String uri;

    @NotBlank
    @Length(max = 200)
    private String iframe;

    @NotBlank
    @Length(max = 45)
    private String icon;

    @NotNull
    private Boolean ativo;

    @NotNull
    private Boolean novaGuia;

    @Column(name = "empresa_id")
    private Integer empresaId;

    @Column(name = "iframe_category_id")
    private Integer iframeCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "iframe_category_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    IframeCategory iframeCategory;

}
