package br.net.unicom.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.net.unicom.backend.model.Venda;
import br.net.unicom.backend.model.projection.VendaResumidaProjection;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findAll();

    Optional<Venda> findByVendaId(Integer vendaId);

    Optional<Venda> findByVendaIdAndEmpresaId(Integer vendaId, Integer empresaId);

    List<Venda> findAllByEmpresaId(Integer empresaId);

    @Query(value = "SELECT venda_id as vendaId,\n" + //
                "vendedor_id as vendedorId,\n" + //
                "supervisor_id as supervisorId\n" + //
                "FROM venda\n" + //
                "WHERE ((data_venda BETWEEN :dataInicio AND :dataFim) OR (:tipoData <> \"DATA_VENDA\")) AND\n" + //
                "((data_agendamento BETWEEN :dataInicio AND :dataFim) OR (:tipoData <> \"DATA_AGENDAMENTO\")) AND\n" + //
                "((data_ativacao BETWEEN :dataInicio AND :dataFim) OR (:tipoData <> \"DATA_ATIVACAO\")) AND\n" + //
                "((data_instalacao BETWEEN :dataInicio AND :dataFim) OR (:tipoData <> \"DATA_INSTALACAO\")) AND\n" + //
                "((data_cadastro BETWEEN :dataInicio AND :dataFim) OR (:tipoData <> \"DATA_CADASTRO\")) AND\n" + //
                "((data_status BETWEEN :dataInicio AND :dataFim) OR (:tipoData <> \"DATA_STATUS\")) AND\n" + //
                "empresa_id = :empresaId AND status_id IN (:statusIdList)", nativeQuery = true)
    List<VendaResumidaProjection> findAllByEmpresaIdAndTipoDataAndDataInicioAndDataFimAndStatusIdList(Integer empresaId, String tipoData, LocalDate dataInicio, LocalDate dataFim, List<Integer> statusIdList);

    /*@Query(value = "", nativeQuery = true)
    List<VendaResumidaProjection> findAllByEmpresaId();*/

}
