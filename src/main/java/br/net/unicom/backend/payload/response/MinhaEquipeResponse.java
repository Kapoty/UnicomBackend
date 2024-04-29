package br.net.unicom.backend.payload.response;

import java.util.List;

import br.net.unicom.backend.model.Usuario;
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

    private Usuario supervisor;

    private Usuario gerente;

    private List<UsuarioMinhaEquipeResponse> usuarioList;

}
