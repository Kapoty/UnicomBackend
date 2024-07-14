package br.net.unicom.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString @AllArgsConstructor
public class GoogleSheetsVenda {

    @JsonIgnore
    private Integer rowId;

    @JsonProperty("ID")
    private Integer vendaId;

    @JsonProperty("Data do Status")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataStatus;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Data do Cadastro")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataCadastro;

    @JsonProperty("Tipo")
    private String tipoProduto;

    @JsonProperty("PDV")
    private String pdv;

    @JsonProperty("Safra")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM yyyy")
    private LocalDate safra;

    @JsonProperty("Data da Venda")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataVenda;

    @JsonProperty("Login Vendedor")
    private String loginVendedor;

    @JsonProperty("Cadastrador")
    private String cadastrador;

    @JsonProperty("Sistema")
    private String sistema;

    @JsonProperty("Ordem")
    private String ordem;

    @JsonProperty("Auditor")
    private String auditor;

    @JsonProperty("OS")
    private String os;

    @JsonProperty("Cust-Code")
    private String custcode;

    @JsonProperty("Mailing/Origem")
    private String origem;

    @JsonProperty("Vendedor")
    private String vendedor;

    @JsonProperty("Supervisor")
    private String supervisor;

    @JsonProperty("Vendedor Externo")
    private String vendedorExterno;

    @JsonProperty("Supervisor Externo")
    private String supervisorExterno;

    @JsonProperty("Auditor Externo")
    private String auditorExterno;

    @JsonProperty("Cadastrador Externo")
    private String cadastradorExterno;

    @JsonProperty("N° de Produtos")
    private Integer totalDeProdutos;

    @JsonProperty("Produto")
    private String produto;

    @JsonProperty("Valor")
    private String valor;

    @JsonProperty("Quantidade")
    private String quantidade;

    @JsonProperty("Telefone Fixo")
    private String telefoneFixo;

    @JsonProperty("Valor Telefone Fixo")
    private String valorTelefoneFixo;

    @JsonProperty("Número Telefone Fixo")
    private String numeroTelefoneFixo;

    @JsonProperty("Tipo de Linha")
    private String tipoDeLinha;

    @JsonProperty("DDD")
    private String ddd;

    @JsonProperty("Operadora")
    private String operadora;

    @JsonProperty("UF")
    private String uf;

    @JsonProperty("Cidade")
    private String cidade;

    @JsonProperty("Bairro")
    private String bairro;

    @JsonProperty("CPF/CNPJ")
    private String cpf;

    @JsonProperty("Nome/Razão Social")
    private String nome;

    @JsonProperty("Nome Contato")
    private String nomeContato;

    @JsonProperty("Contato 1")
    private String contato1;

    @JsonProperty("Contato 2")
    private String contato2;

    @JsonProperty("Contato 3")
    private String contato3;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("Observação")
    private String observacao;

    @JsonProperty("Forma de Pagamento")
    private String formaDePagamento;

    @JsonProperty("Vencimento")
    private Integer vencimento;

    @JsonProperty("Data de Agendamento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataAgendamento;

    @JsonProperty("Data de Instalação")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataInstalacao;

    @JsonProperty("Data da Ativação")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataAtivacao;

    @JsonProperty("Venda Original")
    private String vendaOriginal;

    @JsonProperty("Biometria")
    private String brscan;

    @JsonProperty("Suporte")
    private String suporte;

    @JsonProperty("Prints")
    private String prints;

    @JsonProperty("Situação")
    private String situacao;

    @JsonProperty("M1 - Mês")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM yyyy")
    private LocalDate m1Mes;

    @JsonProperty("M1 - Status")
    private String m1Status;

    @JsonProperty("M1 - Valor")
    private String m1Valor;

    @JsonProperty("M2 - Mês")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM yyyy")
    private LocalDate m2Mes;

    @JsonProperty("M2 - Status")
    private String m2Status;

    @JsonProperty("M2 - Valor")
    private String m2Valor;

    @JsonProperty("M3 - Mês")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM yyyy")
    private LocalDate m3Mes;

    @JsonProperty("M3 - Status")
    private String m3Status;

    @JsonProperty("M3 - Valor")
    private String m3Valor;

    @JsonProperty("M4 - Mês")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM yyyy")
    private LocalDate m4Mes;

    @JsonProperty("M4 - Status")
    private String m4Status;

    @JsonProperty("M4 - Valor")
    private String m4Valor;

    @JsonProperty("M5 - Mês")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM yyyy")
    private LocalDate m5Mes;

    @JsonProperty("M5 - Status")
    private String m5Status;

    @JsonProperty("M5 - Valor")
    private String m5Valor;

    @JsonProperty("M6 - Mês")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM yyyy")
    private LocalDate m6Mes;

    @JsonProperty("M6 - Status")
    private String m6Status;

    @JsonProperty("M6 - Valor")
    private String m6Valor;

    @JsonProperty("M7 - Mês")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM yyyy")
    private LocalDate m7Mes;

    @JsonProperty("M7 - Status")
    private String m7Status;

    @JsonProperty("M7 - Valor")
    private String m7Valor;

    @JsonProperty("M8 - Mês")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM yyyy")
    private LocalDate m8Mes;

    @JsonProperty("M8 - Status")
    private String m8Status;

    @JsonProperty("M8 - Valor")
    private String m8Valor;

    @JsonProperty("M9 - Mês")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM yyyy")
    private LocalDate m9Mes;

    @JsonProperty("M9 - Status")
    private String m9Status;

    @JsonProperty("M9 - Valor")
    private String m9Valor;

    @JsonProperty("M10 - Mês")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM yyyy")
    private LocalDate m10Mes;

    @JsonProperty("M10 - Status")
    private String m10Status;

    @JsonProperty("M10 - Valor")
    private String m10Valor;

    @JsonProperty("M11 - Mês")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM yyyy")
    private LocalDate m11Mes;

    @JsonProperty("M11 - Status")
    private String m11Status;

    @JsonProperty("M11 - Valor")
    private String m11Valor;

    @JsonProperty("M12 - Mês")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM yyyy")
    private LocalDate m12Mes;

    @JsonProperty("M12 - Status")
    private String m12Status;

    @JsonProperty("M12 - Valor")
    private String m12Valor;
    
    @JsonProperty("M13 - Mês")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM yyyy")
    private LocalDate m13Mes;

    @JsonProperty("M13 - Status")
    private String m13Status;

    @JsonProperty("M13 - Valor")
    private String m13Valor;

    @JsonProperty("M14 - Mês")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM yyyy")
    private LocalDate m14Mes;

    @JsonProperty("M14 - Status")
    private String m14Status;

    @JsonProperty("M14 - Valor")
    private String m14Valor;
}  
