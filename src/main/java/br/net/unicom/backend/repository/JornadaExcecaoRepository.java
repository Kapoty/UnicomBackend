package br.net.unicom.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.net.unicom.backend.model.JornadaExcecao;
import br.net.unicom.backend.model.projection.DataProjection;

public interface JornadaExcecaoRepository extends JpaRepository<JornadaExcecao, Long> {

    List<JornadaExcecao> findAll();

    Optional<JornadaExcecao> findByJornadaExcecaoId(Integer jornadaExcecaoId);

    Optional<JornadaExcecao> findByUsuarioIdAndData(Integer usuarioId, LocalDate data);

    @Query(value = "SELECT data FROM jornada_excecao WHERE usuario_id = :usuarioId", nativeQuery = true)
    List<DataProjection> getDataListByUsuarioId(@Param("usuarioId") Integer usuarioId);

}
