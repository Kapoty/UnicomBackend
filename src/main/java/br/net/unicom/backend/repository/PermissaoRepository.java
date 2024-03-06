package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.net.unicom.backend.model.Permissao;

public interface PermissaoRepository extends JpaRepository<Permissao, Long> {

    Optional<Permissao> findByPermissaoId(Integer permissaoId);
    Optional<Permissao> findByNome(String nome);
    
    @Query(value = "SELECT * FROM Permissao p WHERE p.permissao_id IN (SELECT permissao_id FROM Empresa_Permissao p WHERE p.empresa_id = :empresaId)", nativeQuery = true)    
    List<Permissao> findAllByEmpresaId(@Param("empresaId") Integer empresaId);

    @Query(value = "SELECT * FROM Permissao p WHERE p.permissao_id IN (SELECT permissao_id FROM Papel_Empresa_Permissao p WHERE p.papel_id = :papelId)", nativeQuery = true)    
    List<Permissao> findAllByPapelId(@Param("papelId") Integer papelId);

    @Query(value = "SELECT * FROM Permissao p WHERE p.permissao_id IN (SELECT permissao_id FROM Papel_Empresa_Permissao WHERE papel_id IN (SELECT papel_id FROM Usuario_Papel WHERE usuario_id = :usuarioId))", nativeQuery = true)    
    List<Permissao> findAllByUsuarioId(@Param("usuarioId") Integer usuarioId);
}
