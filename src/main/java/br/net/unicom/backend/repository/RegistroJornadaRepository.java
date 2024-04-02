package br.net.unicom.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.RegistroJornada;

public interface RegistroJornadaRepository extends JpaRepository<RegistroJornada, Long> {

    Optional<RegistroJornada> findByRegistroJornadaId(Integer registroJornadaId);

    Optional<RegistroJornada> findByUsuarioIdAndData(Integer usuarioId, LocalDate data);

    List<RegistroJornada> findAllByData(LocalDate data);

}
