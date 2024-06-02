package br.net.unicom.backend.payload.response;

import br.net.unicom.backend.model.Cargo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class RegistroJornadaReportUsuarioResponse {

    private Integer usuarioId;

    private String nome;

    private String nomeCompleto;

    private String cpf;

    private Cargo cargo;

}
