package br.net.unicom.backend.payload.response;

import java.time.LocalDate;
import java.util.List;

import br.net.unicom.backend.model.Cargo;
import br.net.unicom.backend.model.Contrato;
import br.net.unicom.backend.model.Departamento;
import br.net.unicom.backend.model.Empresa;
import br.net.unicom.backend.model.Jornada;
import br.net.unicom.backend.model.Papel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UsuarioResponse {

    private Integer usuarioId;

    private String email;

    private String nome;

    private Boolean ativo;

    private Integer matricula;

    private Integer empresaId;

    private Empresa empresa;

    private List<Papel> papelList;

    private Boolean fotoPerfil;

    private Integer fotoPerfilVersao;

    private LocalDate dataNascimento;

    private String cpf;
    
    private String telefoneCelular;

    private String whatsapp;

    private LocalDate dataContratacao;

    private Integer cargoId;

    private Cargo cargo;

    private Integer contratoId;

    private Contrato contrato;

    private Integer departamentoId;

    private Departamento departamento;

    private Integer jornadaId;

    private Jornada jornada;

}
