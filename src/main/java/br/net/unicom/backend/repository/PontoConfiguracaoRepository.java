package br.net.unicom.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.PontoConfiguracao;

public interface PontoConfiguracaoRepository extends JpaRepository<PontoConfiguracao, Long> {

    Optional<PontoConfiguracao> findByEmpresaId(Integer empresaId);

}
