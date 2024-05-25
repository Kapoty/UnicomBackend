package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.net.unicom.backend.model.VendaProduto;
import br.net.unicom.backend.model.key.VendaProdutoKey;

public interface VendaProdutoRepository extends JpaRepository<VendaProduto, VendaProdutoKey> {

    List<VendaProduto> findAll();
    
    Optional<VendaProduto> findByVendaProdutoId(VendaProdutoKey vendaProdutoId);

    List<VendaProduto> findAllByVendaProdutoIdIn(List<VendaProdutoKey> vendaProdutoId);

    @Query(value = "SELECT v FROM VendaProduto v WHERE v.vendaProdutoId.vendaId IN :vendaId ORDER BY v.vendaProdutoId.produtoId ASC")
    List<VendaProduto> findAllByVendaIdIn(List<Integer> vendaId);

    List<VendaProduto> findAllByVendaVendaIdIn(List<Integer> vendaId);

}