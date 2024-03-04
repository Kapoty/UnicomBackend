package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.Grupo;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    Optional<Grupo> findByGrupoId(Integer grupoId);
    List<Grupo> findByNome(String nome);

}
