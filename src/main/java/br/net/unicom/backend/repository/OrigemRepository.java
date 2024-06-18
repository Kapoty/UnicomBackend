package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.Origem;

public interface OrigemRepository extends JpaRepository<Origem, Long> {

    List<Origem> findAll();
    
    Optional<Origem> findByOrigemId(Integer origemid);

    List<Origem> findAllByEmpresaId(Integer empresaId);

}