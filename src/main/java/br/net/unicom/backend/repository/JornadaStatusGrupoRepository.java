package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.JornadaStatusGrupo;

public interface JornadaStatusGrupoRepository extends JpaRepository<JornadaStatusGrupo, Long> {

    Optional<JornadaStatusGrupo> findByJornadaStatusGrupoId(Integer jornadaStatusGrupoId);

    List<JornadaStatusGrupo> findAllByEmpresaId(Integer empresaId);

}
