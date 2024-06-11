package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import br.net.unicom.backend.model.VendaVisao;

public interface VendaVisaoRepository extends JpaRepository<VendaVisao, Long> {

    List<VendaVisao> findAll();

    Optional<VendaVisao> findByVendaVisaoId(Integer vendaVisaoId);

    List<VendaVisao> findAllByUsuarioId(Integer usuarioId);

    @Modifying
    @Query(value = "UPDATE venda_visao SET atual = CASE WHEN venda_visao_id = :vendaVisaoId THEN 1 ELSE 0 END WHERE usuario_id = :usuarioId", nativeQuery = true)
    void setUsuarioVendaVisualAtual(Integer usuarioId, Integer vendaVisaoId);

}
