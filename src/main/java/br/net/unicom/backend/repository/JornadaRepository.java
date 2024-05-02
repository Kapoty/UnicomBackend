package br.net.unicom.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.net.unicom.backend.model.Jornada;

public interface JornadaRepository extends JpaRepository<Jornada, Long> {

    List<Jornada> findAll();

    Optional<Jornada> findByUsuarioId(Integer usuarioId);

    @Query(value = "SELECT * FROM jornada WHERE empresa_id = (SELECT empresa_id FROM usuario WHERE usuario_id = :usuarioId) and (usuario_id is null OR usuario_id = :usuarioId)", nativeQuery = true)
    List<Jornada> findAllByUsuarioId(Integer usuarioId);

    @Query(value = "SELECT * FROM jornada WHERE\n" + //
                "empresa_id = (SELECT empresa_id FROM usuario WHERE usuario_id = :usuarioId)" + //
                "and (usuario_id is null OR usuario_id = :usuarioId)" + //
                "    and (data_inicio is null or data_inicio <= :data)" + //
                "    and (data_fim is null or data_fim >= :data)" + //
                "    and (" + //
                "            (DAYOFWEEK(:data) = 1 AND domingo = 1) or" + //
                "            (DAYOFWEEK(:data) = 2 AND segunda = 1) or" + //
                "            (DAYOFWEEK(:data) = 3 AND terca = 1) or" + //
                "            (DAYOFWEEK(:data) = 4 AND quarta = 1) or" + //
                "            (DAYOFWEEK(:data) = 5 AND quinta = 1) or" + //
                "            (DAYOFWEEK(:data) = 6 AND sexta = 1) or" + //
                "            (DAYOFWEEK(:data) = 7 AND sabado = 1)" + //
                ")" + //
                "    ORDER BY prioridade DESC LIMIT 1;", nativeQuery = true)
    Optional<Jornada> findByUsuarioIdAndData(Integer usuarioId, LocalDate data);

}
