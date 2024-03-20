package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.Equipe;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {

    List<Equipe> findAll();
    Optional<Equipe> findByEquipeId(Integer equipeId);
    List<Equipe> findAllByEmpresaId(Integer empresaId);
    List<Equipe> findAllBySupervisorId(Integer supervisorId);

}
