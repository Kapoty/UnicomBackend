package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.VendaStatus;

public interface VendaStatusRepository extends JpaRepository<VendaStatus, Long> {

    List<VendaStatus> findAll();
    
    Optional<VendaStatus> findByVendaStatusId(Integer vendaStatusId);

    List<VendaStatus> findAllByEmpresaId(Integer empresaId);

}