package br.net.unicom.backend.payload.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import br.net.unicom.backend.model.enums.VendaBrscanEnum;
import br.net.unicom.backend.model.enums.VendaFormaDePagamentoEnum;
import br.net.unicom.backend.model.enums.VendaGeneroEnum;
import br.net.unicom.backend.model.enums.VendaInfraEnum;
import br.net.unicom.backend.model.enums.VendaPorteEnum;
import br.net.unicom.backend.model.enums.VendaReimputadoEnum;
import br.net.unicom.backend.model.enums.VendaSuporteEnum;
import br.net.unicom.backend.model.enums.VendaTipoDeContaEnum;
import br.net.unicom.backend.model.enums.VendaTipoPessoaEnum;
import br.net.unicom.backend.model.enums.VendaTipoProdutoEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class VendaPatchRequest {

    @NotBlank
    @Length(max = 500)
    private String relato;

    private Integer vendedorId;

    private Integer supervisorId;

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

    @AssertTrue(message = "não pode ser vazio")
    private boolean isGeneroValid() {
        if (this.tipoPessoa == null || this.tipoPessoa.equals(VendaTipoPessoaEnum.CNPJ))
            return true;
        if (this.genero == null)
            return false;
        return true;
    }

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

    @AssertTrue(message = "não pode ser vazio")
    private boolean isRgOrgaoEmissorValid() {
        if (this.tipoPessoa == null || this.tipoPessoa.equals(VendaTipoPessoaEnum.CNPJ))
            return true;
        if (this.rgOrgaoEmissor.isBlank())
            return false;
        return true;
    }

    private LocalDate rgDataEmissao;
    
    @AssertTrue(message = "não pode ser nula")
    private boolean isRgDataEmissaoValid() {
        if (this.tipoPessoa == null || this.tipoPessoa.equals(VendaTipoPessoaEnum.CNPJ))
            return true;
        if (this.rgDataEmissao == null)
            return false;
        return true;
    }

    @NotNull
    @Length(max = 200)
    private String nomeDaMae;
    
    @AssertTrue(message = "não pode ser vazio")
    private boolean isNomeDaMaeValid() {
        if (this.tipoPessoa == null || this.tipoPessoa.equals(VendaTipoPessoaEnum.CNPJ))
            return true;
        if (this.nomeDaMae.isBlank())
            return false;
        return true;
    }

    @NotBlank
    @Length(max = 200)
    private String nomeContato;

    @NotNull
    @Length(min= 10, max = 11)
    private String contato1;

    @NotNull
    @Length(min= 10, max = 11)
    private String contato2;

    @NotNull
    @Length(max = 11)
    private String contato3;

    @NotNull
    private LocalDateTime dataPreferenciaInstalacao1;

    @NotNull
    private LocalDateTime dataPreferenciaInstalacao2;

    @NotBlank
    @Email
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

    @AssertTrue(message = "não pode ser nula")
    private boolean isDataEmissaoValid() {
        if (this.tipoPessoa == null || this.tipoPessoa.equals(VendaTipoPessoaEnum.CPF))
            return true;
        if (this.dataEmissao == null)
            return false;
        return true;
    }

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

    @NotNull
    private Integer sistemaId;

    @NotNull
    @Length(max = 50)
    private String ordem;

    @NotBlank
    @Length(max = 100)
    private String origem;

    @Enumerated(EnumType.STRING)
    private VendaInfraEnum infra;

    @AssertTrue(message = "inválido")
    private boolean isInfraValid() {
        if (this.tipoProduto == null || this.tipoProduto.equals(VendaTipoProdutoEnum.MOVEL))
            return true;
        if (this.infra == null)
            return false;
        return true;
    }

    @NotNull
    private LocalDateTime dataVenda;

    @NotNull
    private VendaReimputadoEnum reimputado;

    @NotNull
    private LocalDate safra;

    private LocalDateTime dataAtivacao;

    private Integer auditorId;

    private Integer cadastradorId;

    private Integer agenteBiometriaId;

    private Integer agenteSuporteId;

    @NotNull
    private Boolean prints;

    private LocalDateTime dataAgendamento;

    private LocalDateTime dataInstalacao;

    @NotBlank
    @Length(max = 50)
    private String pdv;

    @NotNull
    private Boolean vendaOriginal;

    private VendaBrscanEnum brscan;

    private VendaSuporteEnum suporte;

    @NotNull
    @Length(max = 50)
    private String loginVendedor;

    @NotNull
    @Length(max = 50)
    private String operadora;

    @AssertTrue(message = "não pode ser vazio")
    private boolean isOperadoraValid() {
        if (this.tipoProduto == null || this.tipoProduto.equals(VendaTipoProdutoEnum.MOVEL))
            return true;
        if (this.operadora.isBlank())
            return false;
        return true;
    }

    @NotNull
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

    private VendaTipoDeContaEnum tipoDeConta;

    @NotNull
    @Length(max = 500)
    private String observacao;

    @NotNull
    @Length(max = 100)
    private String vendedorExterno;

    @NotNull
    @Length(max = 100)
    private String supervisorExterno;

    @NotNull
    @Length(max = 100)
    private String auditorExterno;

    @NotNull
    @Length(max = 100)
    private String cadastradorExterno;

    @NotNull
    @Length(max = 100)
    private String agenteBiometriaExterno;

    @NotNull
    @Length(max = 100)
    private String agenteSuporteExterno;

    @NotEmpty
    private List<@Valid VendaProdutoRequest> produtoList;

    private List<@Valid VendaFaturaRequest> faturaList;

    private List<@Valid VendaSuporteRequest> suporteList;

}