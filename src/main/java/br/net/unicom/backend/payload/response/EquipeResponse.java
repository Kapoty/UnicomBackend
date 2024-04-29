package br.net.unicom.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class EquipeResponse {

    private Integer equipeId;

    private String nome;

    private int empresaId;

    private UsuarioEquipeResponse supervisor;

    private UsuarioEquipeResponse gerente;

}
