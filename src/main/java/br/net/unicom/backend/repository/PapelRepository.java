package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.Papel;

public interface PapelRepository extends JpaRepository<Papel, Long> {

    List<Papel> findAll();
    Optional<Papel> findByPapelId(Integer papelId);
    List<Papel> findAllByEmpresaId(Integer empresaId);

}
