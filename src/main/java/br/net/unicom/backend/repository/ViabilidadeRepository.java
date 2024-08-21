package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.Viabilidade;

public interface ViabilidadeRepository extends JpaRepository<Viabilidade, Long> {

    List<Viabilidade> findAll();
    
    Optional<Viabilidade> findByViabilidadeId(Integer viabilidadeId);

    List<Viabilidade> findAllByEmpresaId(Integer empresaId);

}