package br.net.unicom.backend.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.RegistroPonto;

public interface RegistroPontoRepository extends JpaRepository<RegistroPonto, Long> {

    Optional<RegistroPonto> findByRegistroPontoId(Integer registroPontoId);

    Optional<RegistroPonto> findByUsuarioIdAndData(Integer usuarioId, LocalDate data);

}
