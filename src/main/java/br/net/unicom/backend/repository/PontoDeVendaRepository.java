package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.PontoDeVenda;

public interface PontoDeVendaRepository extends JpaRepository<PontoDeVenda, Long> {

    List<PontoDeVenda> findAll();
    
    Optional<PontoDeVenda> findByPontoDeVendaId(Integer pontoDeVendaId);

    List<PontoDeVenda> findAllByEmpresaId(Integer empresaId);

}