package br.net.unicom.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vendaId;

    @Column(name = "empresa_id", nullable = false)
    private Integer empresaId;

    @ManyToOne
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

    @NotNull
    @Length(max = 200)
    private String nome;

    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
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
    @Length(max = 14)
    private String cnpj;
    
    @Enumerated(EnumType.STRING)
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

    @NotNull
    @Length(max = 50)
    private String custcode;

    @Enumerated(EnumType.STRING)
    private VendaSistemaEnum sistema;

    @NotNull
    @Length(max = 100)
    private String origem;

    @NotNull
    private LocalDateTime dataVenda;

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

    @NotNull
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("vendaProdutoId.produtoId")
    private List<VendaProduto> produtoList = null;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("vendaFaturaId.vendaFaturaId")
    private List<VendaFatura> faturaList = null;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("vendaAtualizacaoId")
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
