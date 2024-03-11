package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.net.unicom.backend.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsuarioId(Integer usuarioId);

    Boolean existsByEmail(String email);

    Optional<Usuario> findByUsuarioIdAndEmpresaId(Integer usuarioId, Integer empresaId);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailAndAtivo(String email, Boolean ativo);

    List<Usuario> findAllByEmpresaId(Integer empresaId);

    @Query(value = "SELECT usuario_id FROM Usuario WHERE matricula = :matricula and empresa_id = :empresaId", nativeQuery = true)
    Integer getUsuarioIdByMatriculaAndEmpresaId(@Param("matricula") Integer matricula,@Param("empresaId") Integer empresaId);

    @Query(value = "SELECT usuario_id FROM Usuario WHERE email = :email", nativeQuery = true)
    Integer getUsuarioIdByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM Usuario WHERE usuario.matricula = :matricula and usuario.empresa_id = (SELECT empresa_id FROM Dominio WHERE dominio.dominio = :dominio)", nativeQuery = true)
    Optional<Usuario> findByMatriculaAndDominio(@Param("matricula") Integer matricula, @Param("dominio") String dominio);

}
