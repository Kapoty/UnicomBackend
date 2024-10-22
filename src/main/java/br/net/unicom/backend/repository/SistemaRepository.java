package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.Sistema;

public interface SistemaRepository extends JpaRepository<Sistema, Long> {

    List<Sistema> findAll();
    
    Optional<Sistema> findBySistemaId(Integer sistemaId);

    Optional<Sistema> findBySistemaIdAndEmpresaId(Integer sistemaId, Integer empresaId);

    List<Sistema> findAllByEmpresaId(Integer empresaId);

}