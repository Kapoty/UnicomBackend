package br.net.unicom.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.net.unicom.backend.model.Venda;
import br.net.unicom.backend.model.projection.VendaAtoresProjection;
import br.net.unicom.backend.model.projection.VendaDataStatusProjection;
import br.net.unicom.backend.model.projection.VendaResumidaProjection;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findAll();

    Optional<Venda> findByVendaId(Integer vendaId);

    @Query(value = "SELECT * FROM venda WHERE ((os != '' AND os = :os) OR (custcode != '' AND custcode = :custcode) OR (ordem != '' AND ordem = :ordem)) AND empresa_id = :empresaId LIMIT 1", nativeQuery = true)
    Optional<Venda> findByOsOrCustcodeOrOrdemAndEmpresaId(String os, String custcode, String ordem, Integer empresaId);

    Optional<Venda> findByVendaIdAndEmpresaId(Integer vendaId, Integer empresaId);

    List<Venda> findAllByEmpresaId(Integer empresaId);

    @Query(value = "SELECT venda_id as vendaId,\n" + //
                "vendedor_id as vendedorId,\n" + //
                "supervisor_id as supervisorId\n" + //
                "FROM venda\n" + //
                "WHERE " + //
                "(:tipoProduto OR :pdv OR :os OR :cpf OR :nome OR 1) AND" + //
                "((:tipoProduto is NULL) OR (tipo_produto = :tipoProduto)) AND\n" + //
                "(LOWER(pdv) LIKE CONCAT('%',:pdv,'%') ) AND\n" + //
                "((:safra is NULL) OR (year(safra) = year(:safra) AND month(safra) = month(:safra))) AND\n" + //
                "((:tipoData <> \"DATA_VENDA\") OR (data_venda BETWEEN :dataInicio AND :dataFim)) AND\n" + //
                "((:tipoData <> \"DATA_AGENDAMENTO\") OR (data_agendamento BETWEEN :dataInicio AND :dataFim)) AND\n" + //
                "((:tipoData <> \"DATA_ATIVACAO\") OR (data_ativacao BETWEEN :dataInicio AND :dataFim)) AND\n" + //
                "((:tipoData <> \"DATA_INSTALACAO\") OR (data_instalacao BETWEEN :dataInicio AND :dataFim)) AND\n" + //
                "((:tipoData <> \"DATA_CADASTRO\") OR (data_cadastro BETWEEN :dataInicio AND :dataFim)) AND\n" + //
                "((:tipoData <> \"DATA_STATUS\") OR (data_status BETWEEN :dataInicio AND :dataFim)) AND\n" + //
                "(LOWER(os) LIKE CONCAT('%',:os,'%') ) AND\n" + //
                "(LOWER(cpf) LIKE CONCAT('%',:cpf,'%') OR LOWER(cnpj) LIKE CONCAT('%',:cpf,'%')) AND\n" + //
                "(LOWER(nome) LIKE CONCAT('%',:nome,'%') OR LOWER(razao_social) LIKE CONCAT('%',:nome,'%')) AND\n" + //
                "empresa_id = :empresaId AND status_id IN (:statusIdList) LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<VendaAtoresProjection> findAllByEmpresaIdAndFilters(
        Integer empresaId,
        String tipoProduto,
        String pdv,
        LocalDate safra,
        String tipoData,
        LocalDate dataInicio,
        LocalDate dataFim,
        List<Integer> statusIdList,
        String os,
        String cpf,
        String nome,
        Integer offset,
        Integer limit
    );

    @Query(value = "SELECT venda_id as vendaId,\n" + //
                "vendedor_id as vendedorId,\n" + //
                "supervisor_id as supervisorId\n" + //
                "FROM venda\n" + //
                "WHERE " + //
                "(:tipoProduto OR :pdv OR :os OR :cpf OR :nome OR 1) AND" + //
                "((:tipoProduto is NULL) OR (tipo_produto = :tipoProduto)) AND\n" + //
                "(LOWER(pdv) LIKE CONCAT('%',:pdv,'%') ) AND\n" + //
                "((:safra is NULL) OR (year(safra) = year(:safra) AND month(safra) = month(:safra))) AND\n" + //
                "((:tipoData <> \"DATA_VENDA\") OR (data_venda BETWEEN :dataInicio AND :dataFim)) AND\n" + //
                "((:tipoData <> \"DATA_AGENDAMENTO\") OR (data_agendamento BETWEEN :dataInicio AND :dataFim)) AND\n" + //
                "((:tipoData <> \"DATA_ATIVACAO\") OR (data_ativacao BETWEEN :dataInicio AND :dataFim)) AND\n" + //
                "((:tipoData <> \"DATA_INSTALACAO\") OR (data_instalacao BETWEEN :dataInicio AND :dataFim)) AND\n" + //
                "((:tipoData <> \"DATA_CADASTRO\") OR (data_cadastro BETWEEN :dataInicio AND :dataFim)) AND\n" + //
                "((:tipoData <> \"DATA_STATUS\") OR (data_status BETWEEN :dataInicio AND :dataFim)) AND\n" + //
                "(LOWER(os) LIKE CONCAT('%',:os,'%') ) AND\n" + //
                "(LOWER(cpf) LIKE CONCAT('%',:cpf,'%') OR LOWER(cnpj) LIKE CONCAT('%',:cpf,'%')) AND\n" + //
                "(LOWER(nome) LIKE CONCAT('%',:nome,'%') OR LOWER(razao_social) LIKE CONCAT('%',:nome,'%')) AND\n" + //
                "empresa_id = :empresaId AND status_id IN (:statusIdList)" + //
                "AND (:verTodasVendas = true OR ((vendedor_id IN :usuarioIdList) OR (supervisor_id IN :usuarioIdList)))" + //
                "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<VendaAtoresProjection> findAllByEmpresaIdAndFiltersAndUsuarioIdList(
        Integer empresaId,
        String tipoProduto,
        String pdv,
        LocalDate safra,
        String tipoData,
        LocalDate dataInicio,
        LocalDate dataFim,
        List<Integer> statusIdList,
        String os,
        String cpf,
        String nome,
        Integer offset,
        Integer limit,
        Boolean verTodasVendas,
        List<Integer> usuarioIdList
    );

    List<VendaDataStatusProjection> findAllVendaDataStatusProjectionByEmpresaId(Integer empresaId);

    Optional<VendaDataStatusProjection> findVendaDataStatusProjectionByEmpresaIdAndVendaId(Integer empresaId, Integer vendaId);

    List<VendaDataStatusProjection> findAllVendaDataStatusProjectionByEmpresaIdAndVendaIdIn(Integer empresaId, List<Integer> vendaIdList);

    List<Venda> findAllByVendaIdIn(List<Integer> vendaId);

    @Query(value = "SELECT venda_id as vendaId FROM venda WHERE venda_id IN :vendaId", nativeQuery = true)
    List<VendaResumidaProjection> getVendaResumidaProjectionListByVendaIdIn(List<Integer> vendaId);

    @Query(value = "SELECT * FROM venda WHERE empresa_id = :empresaId LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Venda> findAllByEmpresaIdAndLimit(Integer empresaId, Integer offset, Integer limit);

    /*@Query(value = "", nativeQuery = true)
    List<VendaResumidaProjection> findAllByEmpresaId();*/

}
