package br.net.unicom.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.net.unicom.backend.model.VendaAtualizacao;

public interface VendaAtualizacaoRepository extends JpaRepository<VendaAtualizacao, Long> {

    List<VendaAtualizacao> findAll();

    Optional<VendaAtualizacao> findByVendaAtualizacaoId(Integer vendaAtualizacaoId);

    @Query(value = "SELECT va.*, empresa_id, vendedor_id, supervisor_id\n" + //
                "FROM venda_atualizacao va LEFT JOIN venda v ON va.venda_id = v.venda_id\n" + //
                "WHERE " + //
                "(data > :dataInicio) AND\n" + //
                "empresa_id = :empresaId AND\n" + //
                "(:verTodasVendas = true OR ((vendedor_id IN :usuarioIdList) OR (supervisor_id IN :usuarioIdList)))\n"  + //
                "ORDER BY data ASC", nativeQuery = true)
    List<VendaAtualizacao> findAllByEmpresaIdAndFiltersAndUsuarioIdList(
        Integer empresaId,
        LocalDateTime dataInicio,
        Boolean verTodasVendas,
        List<Integer> usuarioIdList
    );

}