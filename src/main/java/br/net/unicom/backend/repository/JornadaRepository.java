package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.Jornada;

public interface JornadaRepository extends JpaRepository<Jornada, Long> {

    List<Jornada> findAll();
    Optional<Jornada> findByUsuarioUsuarioId(Integer usuarioId);

}
