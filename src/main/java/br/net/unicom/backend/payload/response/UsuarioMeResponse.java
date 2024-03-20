package br.net.unicom.backend.payload.response;

import java.util.List;

import br.net.unicom.backend.model.Cargo;
import br.net.unicom.backend.model.Departamento;
import br.net.unicom.backend.model.Empresa;
import br.net.unicom.backend.model.Equipe;
import br.net.unicom.backend.model.Papel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UsuarioMeResponse {

    private Integer usuarioId;

    private String email;

    private String nome;

    private Boolean ativo;

    private Integer matricula;

    private Integer empresaId;

    private Empresa empresa;

    private List<Papel> papelList;;

    private List<String> permissaoList;

    private Boolean fotoPerfil;

    private Integer fotoPerfilVersao;

    private Departamento departamento;

    private Equipe equipe;

    private Cargo cargo;

}
