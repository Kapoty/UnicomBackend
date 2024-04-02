package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.Cargo;

public interface CargoRepository extends JpaRepository<Cargo, Long> {

    List<Cargo> findAll();
    Optional<Cargo> findByCargoId(Integer cargoId);
    List<Cargo> findAllByEmpresaId(Integer empresaId);

}
