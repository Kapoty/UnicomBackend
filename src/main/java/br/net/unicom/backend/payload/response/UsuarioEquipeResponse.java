package br.net.unicom.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UsuarioEquipeResponse {

    private Integer usuarioId;

    private String nome;

    private Boolean fotoPerfil;

    private Integer fotoPerfilVersao;

    private Integer matricula;

}
