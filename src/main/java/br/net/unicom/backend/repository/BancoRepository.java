package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.Banco;

public interface BancoRepository extends JpaRepository<Banco, Long> {

    List<Banco> findAll();
    
    Optional<Banco> findByBancoId(Integer bancoId);

    Optional<Banco> findByBancoIdAndEmpresaId(Integer bancoId, Integer empresaId);

    List<Banco> findAllByEmpresaId(Integer empresaId);

}