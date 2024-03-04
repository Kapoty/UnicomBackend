package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsuarioId(Integer usuarioId);
    List<Usuario> findAllByEmpresaId(Integer empresaId);
    Optional<Usuario> findByEmpresaIdAndUsuarioId(Integer empresaId, Integer usuarioId);

}
