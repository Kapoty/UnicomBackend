package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.net.unicom.backend.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsuarioId(Integer usuarioId);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailAndAtivo(String email, Boolean ativo);
    List<Usuario> findAllByEmpresaId(Integer empresaId);
    Boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM Usuario WHERE usuario.matricula = :matricula and usuario.empresa_id = (SELECT empresa_id FROM Dominio WHERE dominio.dominio = :dominio)", nativeQuery = true)
    Optional<Usuario> findByMatriculaAndDominio(@Param("matricula") Integer matricula, @Param("dominio") String dominio);

}
