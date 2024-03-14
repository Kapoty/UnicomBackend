package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.net.unicom.backend.model.Papel;
import br.net.unicom.backend.model.Permissao;

public interface PapelRepository extends JpaRepository<Papel, Long> {

    List<Papel> findAll();
    Optional<Papel> findByPapelId(Integer papelId);
    List<Papel> findAllByEmpresaId(Integer empresaId);


    @Query(value = "SELECT * FROM Papel p WHERE p.papel_id IN (SELECT papel_id FROM Usuario_Papel WHERE usuario_id = :usuarioId)", nativeQuery = true)    
    List<Papel> findAllByUsuarioId(@Param("usuarioId") Integer usuarioId);

}
