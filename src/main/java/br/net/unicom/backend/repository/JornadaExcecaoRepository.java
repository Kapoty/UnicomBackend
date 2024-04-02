package br.net.unicom.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.JornadaExcecao;

public interface JornadaExcecaoRepository extends JpaRepository<JornadaExcecao, Long> {

    List<JornadaExcecao> findAll();

    Optional<JornadaExcecao> findByJornadaExcecaoId(Integer jornadaExcecaoId);

    Optional<JornadaExcecao> findByUsuarioIdAndData(Integer usuarioId, LocalDate data);

}
