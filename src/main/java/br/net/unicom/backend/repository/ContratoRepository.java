package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    List<Contrato> findAll();
    Optional<Contrato> findByContratoId(Integer papelId);
    List<Contrato> findAllByEmpresaId(Integer empresaId);

}
