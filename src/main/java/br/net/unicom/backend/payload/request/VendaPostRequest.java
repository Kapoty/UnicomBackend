package br.net.unicom.backend.payload.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import br.net.unicom.backend.model.VendaBrscanEnum;
import br.net.unicom.backend.model.VendaFormaDePagamentoEnum;
import br.net.unicom.backend.model.VendaGeneroEnum;
import br.net.unicom.backend.model.VendaPorteEnum;
import br.net.unicom.backend.model.VendaSistemaEnum;
import br.net.unicom.backend.model.VendaSuporteEnum;
import br.net.unicom.backend.model.VendaTipoPessoaEnum;
import br.net.unicom.backend.model.VendaTipoProdutoEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
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

    @AssertTrue(message = "inválido")
    private boolean isCpfValid() {
        if (this.tipoPessoa == null || this.tipoPessoa.equals(VendaTipoPessoaEnum.CNPJ))
            return true;
        if (this.cpf.length() != 11)
            return false;
        return true;
    }

    @NotNull
    @Length(max = 200)
    private String nome;

    @AssertTrue(message = "não deve ser vazio")
    private boolean isNomeValid() {
        if (this.tipoPessoa == null || this.tipoPessoa.equals(VendaTipoPessoaEnum.CNPJ))
            return true;
        if (this.nome.isBlank())
            return false;
        return true;
    }

    private LocalDate dataNascimento;

    @AssertTrue(message = "não pode ser nula")
    private boolean isDataNascimentoValid() {
        if (this.tipoPessoa == null || this.tipoPessoa.equals(VendaTipoPessoaEnum.CNPJ))
            return true;
        if (this.dataNascimento == null)
            return false;
        return true;
    }

    private VendaGeneroEnum genero;

    @NotNull
    @Length(max = 20)
    private String rg;

    @AssertTrue(message = "não deve ser vazio")
    private boolean isRgValid() {
        if (this.tipoPessoa == null || this.tipoPessoa.equals(VendaTipoPessoaEnum.CNPJ))
            return true;
        if (this.rg.isBlank())
            return false;
        return true;
    }

    @NotNull
    @Length(max = 50)
    private String rgOrgaoEmissor;

    private LocalDate rgDataEmissao;

    @NotNull
    @Length(max = 200)
    private String nomeDaMae;

    @NotBlank
    @Length(max = 200)
    private String nomeContato;

    @NotNull
    @Length(min= 11, max = 11)
    private String contato1;

    @NotNull
    @Length(max = 11)
    private String contato2;

    @NotNull
    @Length(max = 11)
    private String contato3;

    @NotNull
    @Length(max = 200)
    private String email;

    @NotNull
    @Length(max = 14)
    private String cnpj;

    @AssertTrue(message = "inválido")
    private boolean isCnpjValid() {
        if (this.tipoPessoa == null || this.tipoPessoa.equals(VendaTipoPessoaEnum.CPF))
            return true;
        if (this.cnpj.length() != 14)
            return false;
        return true;
    }
    
    private VendaPorteEnum porte;

    @NotNull
    @Length(max = 200)
    private String razaoSocial;
    
    @AssertTrue(message = "não deve ser vazio")
    private boolean isRazaoSocialValid() {
        if (this.tipoPessoa == null || this.tipoPessoa.equals(VendaTipoPessoaEnum.CPF))
            return true;
        if (this.razaoSocial.isBlank())
            return false;
        return true;
    }

    private LocalDate dataConstituicao;

    private LocalDate dataEmissao;

    @NotNull
    @Length(max = 200)
    private String representanteLegal;

    @AssertTrue(message = "não deve ser vazio")
    private boolean isRepresentanteLegalValid() {
        if (this.tipoPessoa == null || this.tipoPessoa.equals(VendaTipoPessoaEnum.CPF))
            return true;
        if (this.representanteLegal.isBlank())
            return false;
        return true;
    }

    @NotNull
    @Length(max = 11)
    private String cpfRepresentanteLegal;

    @AssertTrue(message = "inválido")
    private boolean isCpfRepresentanteLegalValid() {
        if (this.tipoPessoa == null || this.tipoPessoa.equals(VendaTipoPessoaEnum.CPF))
            return true;
        if (this.cpfRepresentanteLegal.length() != 11)
            return false;
        return true;
    }

    @NotNull
    @Length(min = 8, max = 8)
    private String cep;

    @NotBlank
    @Length(max = 100)
    private String logradouro;

    @NotBlank
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

    @NotNull
    @Length(max = 50)
    private String custcode;

    private Integer sistemaId;

    @NotNull
    @Length(max = 100)
    private String origem;

    private LocalDateTime dataVenda;

    @NotNull
    private Boolean reimputado;

    @NotNull
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
    private VendaBrscanEnum brscan;

    @NotNull
    private VendaSuporteEnum suporte;

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

    private List<@Valid VendaProdutoRequest> produtoList;

    private List<@Valid VendaFaturaRequest> faturaList;

}