package br.net.unicom.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.RegistroJornadaCorrecao;
import br.net.unicom.backend.model.key.RegistroJornadaCorrecaoKey;


public interface RegistroJornadaCorrecaoRepository extends JpaRepository<RegistroJornadaCorrecao, RegistroJornadaCorrecaoKey> {

    Optional<RegistroJornadaCorrecao> findByRegistroJornadaCorrecaoKey(RegistroJornadaCorrecaoKey registroJornadaCorrecaoKey);
}
