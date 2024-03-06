package br.net.unicom.backend.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class JwtResponse {

    private String token;
    private Integer usuarioId;
    private Integer empresaId;
    private String email;
    private Boolean ativo;
    private List<String> permissoes;

}
