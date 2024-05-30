package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.net.unicom.backend.model.VendaFatura;
import br.net.unicom.backend.model.key.VendaFaturaKey;

public interface VendaFaturaRepository extends JpaRepository<VendaFatura, VendaFaturaKey> {

    List<VendaFatura> findAll();
    
    Optional<VendaFatura> findByVendaFaturaId(VendaFaturaKey vendaFaturaKey);

    @Query(value = "SELECT * FROM venda_fatura WHERE venda_id IN (SELECT venda_id FROM venda WHERE empresa_id = :empresaId) LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<VendaFatura> findAllByEmpresaIdAndLimit(Integer empresaId, Integer offset, Integer limit);

}