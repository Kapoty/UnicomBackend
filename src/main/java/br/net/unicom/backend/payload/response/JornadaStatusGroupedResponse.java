package br.net.unicom.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class JornadaStatusGroupedResponse {

    private Integer jornadaStatusId;

    private String nome;
    
    private Integer maxDuracao;

    private Integer duracao;

    private Integer maxUso;

    private Integer usos;

    private String cor;

}
