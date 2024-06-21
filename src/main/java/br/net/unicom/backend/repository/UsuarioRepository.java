package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.net.unicom.backend.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findAllByAtivoTrue();

    @Query(value = "SELECT usuario_id FROM Usuario WHERE ativo = true", nativeQuery = true)
    List<Integer> findAllUsuarioIdByAtivoTrue();

    Optional<Usuario> findByUsuarioId(Integer usuarioId);

    Optional<Usuario> findByUsuarioIdAndEmpresaId(Integer usuarioId, Integer empresaId);

    List<Usuario> findAllByEmpresaId(Integer empresaId);

    List<Usuario> findAllByEquipeId(Integer equipeId);

    @Query(value = "SELECT usuario_id FROM Usuario WHERE matricula = :matricula and empresa_id = :empresaId", nativeQuery = true)
    Integer getUsuarioIdByMatriculaAndEmpresaId(@Param("matricula") Integer matricula, @Param("empresaId") Integer empresaId);

    @Query(value = "SELECT usuario_id FROM Usuario WHERE email = :email and empresa_id = :empresaId", nativeQuery = true)
    Integer getUsuarioIdByEmailAndEmpresaId(@Param("email") String email, @Param("empresaId") Integer empresaId);

    @Query(value = "SELECT usuario_id FROM Usuario WHERE (usuario.matricula = :login OR usuario.email = :login) and usuario.empresa_id = (SELECT empresa_id FROM Dominio WHERE dominio.dominio = :dominio) and usuario.ativo = true", nativeQuery = true)
    Optional<Integer> findUsuarioIdByLoginAndDominio(String login, String dominio);

}
