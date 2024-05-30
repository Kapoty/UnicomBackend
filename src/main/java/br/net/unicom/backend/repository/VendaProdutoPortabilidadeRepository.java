package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.net.unicom.backend.model.VendaProdutoPortabilidade;
import br.net.unicom.backend.model.key.VendaProdutoPortabilidadeKey;

public interface VendaProdutoPortabilidadeRepository extends JpaRepository<VendaProdutoPortabilidade, VendaProdutoPortabilidadeKey> {

    List<VendaProdutoPortabilidade> findAll();
    
    Optional<VendaProdutoPortabilidade> findByVendaProdutoPortabilidadeId(VendaProdutoPortabilidadeKey vendaProdutoPortabilidadeId);

    @Query(value = "SELECT * FROM venda_produto_portabilidade WHERE venda_id IN (SELECT venda_id FROM venda WHERE empresa_id = :empresaId) LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<VendaProdutoPortabilidade> findAllByEmpresaIdAndLimit(Integer empresaId, Integer offset, Integer limit);

}