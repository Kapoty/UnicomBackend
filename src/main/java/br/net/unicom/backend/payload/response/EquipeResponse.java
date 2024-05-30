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

    private Integer empresaId;

    private Integer supervisorId;

    private UsuarioPublicResponse supervisor;

    private Integer gerenteId;

    private UsuarioPublicResponse gerente;

}
