package br.net.unicom.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.net.unicom.backend.model.RegistroJornadaStatus;

public interface RegistroJornadaStatusRepository extends JpaRepository<RegistroJornadaStatus, Long> {

    Boolean existsByRegistroJornadaId(Integer registroJornadaId);

    @Query(value = "SELECT SUM(TIME_TO_SEC(TIMEDIFF(CASE WHEN fim <> 'null' THEN fim ELSE curtime() END, inicio))) as duracao FROM registro_jornada_status WHERE registro_jornada_id = :registroJornadaId and jornada_status_id = :jornadaStatusId", nativeQuery = true)
    Optional<Integer> getDuracaoSumByJornadaStatusIdAndRegistroJornadaId(@Param(value = "jornadaStatusId") Integer jornadaStatusId, @Param(value = "registroJornadaId") Integer registroJornadaId);

    @Query(value = "SELECT COUNT(*) FROM registro_jornada_status WHERE registro_jornada_id = :registroJornadaId and jornada_status_id = :jornadaStatusId", nativeQuery = true)
    Optional<Integer> getUsosByRegistroJornadaIdAndJornadaStatusId(@Param(value = "registroJornadaId") Integer registroJornadaId, @Param(value = "jornadaStatusId") Integer jornadaStatusId);

}
