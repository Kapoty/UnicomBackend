package br.net.unicom.backend.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class EmpresaPermissao {

    @EmbeddedId
    EmpresaPermissaoKey empresaPermissaoId;
    
    @ManyToOne
    @MapsId("empresaId")
    @JoinColumn(name = "empresa_id")
    Empresa empresa;

    @ManyToOne
    @MapsId("permissaoId")
    @JoinColumn(name = "permissao_id")
    Permissao permissao;

}
