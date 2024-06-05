package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.Adicional;

public interface AdicionalRepository extends JpaRepository<Adicional, Long> {

    List<Adicional> findAll();
    
    Optional<Adicional> findByAdicionalId(Integer adicionalId);

    List<Adicional> findAllByEmpresaId(Integer empresaId);

}