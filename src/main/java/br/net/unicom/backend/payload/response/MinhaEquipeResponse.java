package br.net.unicom.backend.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class MinhaEquipeResponse {

    private Integer equipeId;

    private String nome;

    private int empresaId;

    private int supervisorId;

    private UsuarioPublicResponse supervisor;

    private UsuarioPublicResponse gerente;

    private List<UsuarioPublicResponse> usuarioList;

    private String icon;

    private String iconFilename;

}
