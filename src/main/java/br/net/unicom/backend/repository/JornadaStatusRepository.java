package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.net.unicom.backend.model.JornadaStatus;
import br.net.unicom.backend.model.projection.JornadaStatusGroupedProjection;
import br.net.unicom.backend.model.projection.JornadaStatusOptionProjection;

public interface JornadaStatusRepository extends JpaRepository<JornadaStatus, Long> {

    List<JornadaStatus> findAll();
    
    Optional<JornadaStatus> findByJornadaStatusId(Integer jornadaStatusId);

    Optional<JornadaStatus> findByJornadaStatusIdAndEmpresaId(Integer jornadaStatusId, Integer empresaId);

    List<JornadaStatus> findAllByEmpresaId(Integer empresaId);

    List<JornadaStatus> findAllByEmpresaIdAndUsuarioPodeAtivarTrueAndAtivoTrue(Integer empresaId);

    List<JornadaStatus> findAllByEmpresaIdAndSupervisorPodeAtivarTrueAndAtivoTrue(Integer empresaId);

    @Query(value = "SELECT rjs.jornada_status_id as jornadaStatusId, min(nome) as nome, min(max_duracao) as maxDuracao, SUM(TIME_TO_SEC(TIMEDIFF(CASE WHEN fim <> 'null' THEN fim ELSE curtime() END, inicio))) as duracao, min(max_uso) as maxUso, count(*) as usos, min(cor) as cor FROM registro_jornada_status as rjs LEFT JOIN jornada_status js ON rjs.jornada_status_id = js.jornada_status_id WHERE rjs.registro_jornada_id = :registroJornadaId GROUP BY rjs.jornada_status_id;", nativeQuery = true)
    List<JornadaStatusGroupedProjection> getJornadaStatusGroupedProjectionListByRegistroJornadaId(@Param(value = "registroJornadaId") Integer registroJornadaId);

    @Query(value = "SELECT js.jornada_status_id as jornadaStatusId, min(nome) as nome, min(max_duracao) as maxDuracao, min(max_uso) as maxUso, min(usuario_pode_ativar) as usuarioPodeAtivar, min(supervisor_pode_ativar) as supervisorPodeAtivar, CASE WHEN usos <> 'null' THEN usos ELSE 0 END as usos, min(cor) as cor FROM (SELECT * FROM jornada_status WHERE empresa_id = :empresaId) js LEFT JOIN (SELECT jornada_status_id, count(*) as usos FROM registro_jornada_status WHERE registro_jornada_id = :registroJornadaId GROUP BY jornada_status_id) u ON js.jornada_status_id = u.jornada_status_id GROUP BY js.jornada_status_id;", nativeQuery = true)
    List<JornadaStatusOptionProjection> getJornadaStatusOptionProjectionListByEmpresaIdAndRegistroJornadaId(@Param(value = "empresaId") Integer empresaId, @Param(value = "registroJornadaId") Integer registroJornadaId);

}
