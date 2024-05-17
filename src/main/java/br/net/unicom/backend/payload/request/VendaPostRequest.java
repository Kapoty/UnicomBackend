package br.net.unicom.backend.payload.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import br.net.unicom.backend.model.VendaFormaDePagamentoEnum;
import br.net.unicom.backend.model.VendaGeneroEnum;
import br.net.unicom.backend.model.VendaPorteEnum;
import br.net.unicom.backend.model.VendaSistemaEnum;
import br.net.unicom.backend.model.VendaTipoPessoaEnum;
import br.net.unicom.backend.model.VendaTipoProdutoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class VendaPostRequest {

    @NotNull
    @Length(max = 200)
    private String relato;

    @NotNull
    private VendaTipoPessoaEnum tipoPessoa;

    @NotNull
    private VendaTipoProdutoEnum tipoProduto;

    @NotNull
    private Integer statusId;

    @NotNull
    @Length(max = 11)
    private String cpf;

    @NotNull
    @Length(max = 200)
    private String nome;

    private LocalDate dataNascimento;

    private VendaGeneroEnum genero;

    @NotNull
    @Length(max = 20)
    private String rg;

    @NotNull
    @Length(max = 50)
    private String rgOrgaoEmissor;

    private LocalDate rgDataEmissao;

    @NotNull
    @Length(max = 200)
    private String nomeDaMae;

    @NotNull
    @Length(max = 200)
    private String nomeContato;

    @NotNull
    @Length(max = 11)
    private String telefoneCelular;

    @NotNull
    @Length(max = 11)
    private String telefoneWhatsapp;

    @NotNull
    @Length(max = 11)
    private String telefoneResidencial;

    @NotNull
    @Length(max = 200)
    private String email;

    @NotNull
    @Length(max = 18)
    private String cnpj;
    
    private VendaPorteEnum porte;

    @NotNull
    @Length(max = 200)
    private String razaoSocial;

    private LocalDate dataConstituicao;

    private LocalDate dataEmissao;

    @NotNull
    @Length(max = 200)
    private String representanteLegal;

    @NotNull
    @Length(max = 11)
    private String cpfRepresentanteLegal;

    @NotNull
    @Length(max = 8)
    private String cep;

    @NotNull
    @Length(max = 100)
    private String logradouro;

    @NotNull
    @Length(max = 10)
    private String numero;

    @NotNull
    @Length(max = 100)
    private String complemento;

    @NotNull
    @Length(max = 100)
    private String bairro;

    @NotNull
    @Length(max = 100)
    private String referencia;

    @NotNull
    @Length(max = 100)
    private String cidade;

    @NotNull
    @Length(max = 2)
    private String uf;

    @NotNull
    @Length(max = 50)
    private String os;

    private VendaSistemaEnum sistema;

    @NotNull
    @Length(max = 100)
    private String origem;

    private LocalDateTime dataVenda;

    private LocalDate safra;

    private LocalDateTime dataAtivacao;

    @NotNull
    private Boolean prints;

    private LocalDateTime dataAgendamento;

    private LocalDateTime dataInstalacao;

    @NotNull
    @Length(max = 50)
    private String pdv;

    @NotNull
    private Boolean vendaOriginal;

    @NotNull
    private Boolean brscan;

    @NotNull
    @Length(max = 50)
    private String loginVendedor;

    private VendaFormaDePagamentoEnum formaDePagamento;

    @NotNull
    private Integer vencimento;

    @NotNull
    @Length(max = 50)
    private String agencia;

    @NotNull
    @Length(max = 50)
    private String conta;

    @NotNull
    @Length(max = 50)
    private String banco;

    @NotNull
    @Length(max = 200)
    private String observacao;

    private List<VendaProdutoRequest> produtoList;

    private List<VendaFaturaRequest> faturaList;

}