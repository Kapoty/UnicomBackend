package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.Departamento;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    List<Departamento> findAll();
    Optional<Departamento> findByDepartamentoId(Integer departamentoId);
    List<Departamento> findAllByEmpresaId(Integer empresaId);

}
