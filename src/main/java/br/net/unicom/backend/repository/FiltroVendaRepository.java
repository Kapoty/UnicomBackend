package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import br.net.unicom.backend.model.FiltroVenda;

public interface FiltroVendaRepository extends JpaRepository<FiltroVenda, Long> {

    List<FiltroVenda> findAll();

    Optional<FiltroVenda> findByUsuarioId(@Param("usuarioId") Integer usuarioId);

}
