package br.net.unicom.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.net.unicom.backend.model.key.PapelEmpresaPermissaoKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class PapelEmpresaPermissao {

    @EmbeddedId
    PapelEmpresaPermissaoKey papelEmpresaPermissaoId;
    
    @ManyToOne
    @MapsId("empresaPermissaoId")
    @JoinColumns({
        @JoinColumn(name="empresa_id"),
        @JoinColumn(name="permissao_id")
    })
    EmpresaPermissao empresaPermissao;

    @ManyToOne
    @JoinColumn(name = "papel_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    Papel papel;

}
