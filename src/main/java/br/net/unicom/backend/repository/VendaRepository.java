package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.Venda;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findAll();
    Optional<Venda> findByVendaId(Integer vendaId);
    List<Venda> findAllByEmpresaId(Integer empresaId);

}
