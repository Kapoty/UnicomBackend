package br.net.unicom.backend.payload.request;

import java.util.Optional;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class PatchJornadaRequest {

    private Optional<@Valid JornadaRequest> jornada;

}
