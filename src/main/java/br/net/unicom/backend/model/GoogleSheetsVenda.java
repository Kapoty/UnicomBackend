package br.net.unicom.backend.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString @AllArgsConstructor
public class GoogleSheetsVenda {

    @JsonIgnore
    private Integer rowId;

    private Integer vendaId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataStatus;

    private String cpf;

    private String nome;

    private String cnpj;

    private String razaoSocial;

}
