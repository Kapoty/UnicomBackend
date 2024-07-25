package br.net.unicom.backend.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.net.unicom.backend.model.RegistroJornadaCorrecao;
import br.net.unicom.backend.model.key.RegistroJornadaCorrecaoKey;


public interface RegistroJornadaCorrecaoRepository extends JpaRepository<RegistroJornadaCorrecao, RegistroJornadaCorrecaoKey> {

    Optional<RegistroJornadaCorrecao> findByRegistroJornadaCorrecaoKey(RegistroJornadaCorrecaoKey registroJornadaCorrecaoKey);

    Boolean existsByRegistroJornadaCorrecaoKey(RegistroJornadaCorrecaoKey registroJornadaCorrecaoKey);

    @Query(value = "SELECT COUNT(*) FROM registro_jornada_correcao WHERE usuario_id = :usuarioId AND (data BETWEEN :startData AND :endData) AND aprovada = false", nativeQuery = true)
    Integer getNumeroCorrecoesNaoAprovadas(Integer usuarioId, LocalDate startData, LocalDate endData);
}
