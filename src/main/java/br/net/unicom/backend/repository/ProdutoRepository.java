package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findAll();
    
    Optional<Produto> findByProdutoId(Integer produtoId);

    Optional<Produto> findByProdutoIdAndEmpresaId(Integer produtoId, Integer empresaId);

    List<Produto> findAllByEmpresaId(Integer empresaId);

}