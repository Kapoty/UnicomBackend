package br.net.unicom.backend.model.projection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import br.net.unicom.backend.model.VendaBrscanEnum;
import br.net.unicom.backend.model.VendaFatura;
import br.net.unicom.backend.model.VendaFormaDePagamentoEnum;
import br.net.unicom.backend.model.VendaPorteEnum;
import br.net.unicom.backend.model.VendaProduto;
import br.net.unicom.backend.model.VendaSistemaEnum;
import br.net.unicom.backend.model.VendaSuporteEnum;
import br.net.unicom.backend.model.VendaTipoPessoaEnum;
import br.net.unicom.backend.model.VendaTipoProdutoEnum;

public interface VendaResumidaProjection {

    Integer getVendaId();

    Integer getStatusId();

    VendaTipoPessoaEnum getTipoPessoa();

    String getNome();

    String getRazaoSocial();

    VendaTipoProdutoEnum getTipoProduto();

    LocalDateTime getDataVenda();

    LocalDateTime getDataStatus();

    LocalDateTime getDataAtivacao();

    LocalDateTime getDataAgendamento();
    
    LocalDateTime getDataInstalacao();

    LocalDateTime getDataCadastro();

    String getCpf();
    
    String getCnpj();

    String getPdv();

    LocalDate getSafra();

    String getLoginVendedor();

    Integer getCadastradorId();

    VendaSistemaEnum getSistema();

    Integer getAuditorId();

    String getOs();

    String getCustcode();

    String getOrigem();

    Integer getVendedorId();

    Integer getSupervisorId();

    List<VendaProduto> getProdutoList();

    String getUf();

    String getCidade();

    String getBairro();

    String getNomeContato();

    VendaPorteEnum getPorte();

    String getTelefoneCelular();

    String getTelefoneWhatsapp();

    String getTelefoneResidencial();

    String getEmail();

    String getObservacao();

    VendaFormaDePagamentoEnum getFormaDePagamento();

    Integer getVencimento();

    Boolean getVendaOriginal();

    Boolean getPrints();

    VendaBrscanEnum getBrscan();

    VendaSuporteEnum getSuporte();

    List<VendaFatura> getFaturaList();
}