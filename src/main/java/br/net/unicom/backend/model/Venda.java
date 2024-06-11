package br.net.unicom.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import br.net.unicom.backend.model.enums.VendaBrscanEnum;
import br.net.unicom.backend.model.enums.VendaFormaDePagamentoEnum;
import br.net.unicom.backend.model.enums.VendaGeneroEnum;
import br.net.unicom.backend.model.enums.VendaPorteEnum;
import br.net.unicom.backend.model.enums.VendaReimputadoEnum;
import br.net.unicom.backend.model.enums.VendaSuporteEnum;
import br.net.unicom.backend.model.enums.VendaTipoPessoaEnum;
import br.net.unicom.backend.model.enums.VendaTipoProdutoEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Venda {

    public interface DefaultView {};
    public interface WithProdutoListView {};
    public interface WithFaturaListView {};
    public interface WithAtualizacaoListView {};
    public interface WithProdutoAndFaturaListView extends WithProdutoListView, WithFaturaListView{};
    public interface WithAllListView extends WithProdutoListView, WithFaturaListView, WithAtualizacaoListView{};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vendaId;

    @Column(name = "empresa_id", nullable = false)
    private Integer empresaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    Empresa empresa;

    @NotNull
    LocalDateTime dataCadastro;

    @Column(name = "vendedor_id")
    private Integer vendedorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    Usuario vendedor;

    @Column(name = "supervisor_id")
    private Integer supervisorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    Usuario supervisor;

    @Enumerated(EnumType.STRING)
    @NotNull
    private VendaTipoPessoaEnum tipoPessoa;

    @Enumerated(EnumType.STRING)
    @NotNull
    private VendaTipoProdutoEnum tipoProduto;

    @NotNull
    private LocalDateTime dataStatus;

    @Column(name = "status_id")
    private Integer statusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    VendaStatus status;

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

    @Enumerated(EnumType.STRING)
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

    @NotNull
    @Length(max = 200)
    private String nomeContato;

    @NotBlank
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
    
    @Enumerated(EnumType.STRING)
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

    @Column(name = "sistema_id")
    private Integer sistemaId;

    @NotNull
    @Length(max = 50)
    private String ordem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sistema_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    Sistema sistema;

    @NotBlank
    @Length(max = 100)
    private String origem;

    @NotNull
    private LocalDateTime dataVenda;

    @NotNull
    @Enumerated(EnumType.STRING)
    private VendaReimputadoEnum reimputado;

    @NotNull
    LocalDate safra;

    private LocalDateTime dataAtivacao;

    @Column(name = "auditor_id")
    private Integer auditorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auditor_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    Usuario auditor;

    @Column(name = "cadastrador_id")
    private Integer cadastradorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cadastrador_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    Usuario cadastrador;

    @NotNull
    private Boolean prints;

    private LocalDateTime dataAgendamento;

    private LocalDateTime dataInstalacao;

    @NotBlank
    @Length(max = 50)
    private String pdv;

    @NotNull
    private Boolean vendaOriginal;

    @NotNull
    @Enumerated(EnumType.STRING)
    private VendaBrscanEnum brscan;

    @NotNull
    @Enumerated(EnumType.STRING)
    private VendaSuporteEnum suporte;

    @NotNull
    @Length(max = 50)
    private String loginVendedor;

    @Enumerated(EnumType.STRING)
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

    @NotNull
    @Length(max = 100)
    private String vendedorExterno = "";

    @NotNull
    @Length(max = 100)
    private String supervisorExterno = "";

    @NotNull
    @Length(max = 100)
    private String auditorExterno = "";

    @NotNull
    @Length(max = 100)
    private String cadastradorExterno = "";

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("vendaProdutoId.produtoId")
    @JsonView(WithProdutoListView.class)
    private List<VendaProduto> produtoList = null;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("vendaFaturaId.faturaId")
    @JsonView(WithFaturaListView.class)
    private List<VendaFatura> faturaList = null;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("vendaAtualizacaoId")
    @JsonView(WithAtualizacaoListView.class)
    private List<VendaAtualizacao> atualizacaoList = null;

    public List<VendaProduto> getProdutoList() {
        if (this.produtoList == null)
            this.produtoList = new ArrayList<>();
        return this.produtoList;
    }

    public void setProdutoList(List<VendaProduto> produtoList) {
        if (this.produtoList == null) {
            this.produtoList = produtoList;
        } else {
            this.produtoList.clear();
            this.produtoList.addAll(produtoList);
        }
    }

    public List<VendaFatura> getFaturaList() {
        if (this.faturaList == null)
            this.faturaList = new ArrayList<>();
        return this.faturaList;
    }

    public void setFaturaList(List<VendaFatura> faturaList) {
        if (this.faturaList == null) {
            this.faturaList = faturaList;
        } else {
            this.faturaList.clear();
            this.faturaList.addAll(faturaList);
        }
    }

    public List<VendaAtualizacao> getAtualizacaoList() {
        if (this.atualizacaoList == null)
            this.atualizacaoList = new ArrayList<>();
        return this.atualizacaoList;
    }

    public void setAtualizacaoList(List<VendaAtualizacao> atualizacaoList) {
        if (this.atualizacaoList == null) {
            this.atualizacaoList = atualizacaoList;
        } else {
            this.atualizacaoList.clear();
            this.atualizacaoList.addAll(atualizacaoList);
        }
    }
}
