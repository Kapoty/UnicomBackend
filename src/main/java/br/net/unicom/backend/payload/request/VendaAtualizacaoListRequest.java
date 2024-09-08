package br.net.unicom.backend.payload.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class VendaAtualizacaoListRequest {

    private LocalDateTime dataInicio;

}