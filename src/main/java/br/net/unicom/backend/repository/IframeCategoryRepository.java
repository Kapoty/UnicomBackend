package br.net.unicom.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.net.unicom.backend.model.IframeCategory;

public interface IframeCategoryRepository extends JpaRepository<IframeCategory, Long> {

    List<IframeCategory> findAll();
    Optional<IframeCategory> findByIframeCategoryId(Integer iframeCategoryId);

    @Query(value = "SELECT * FROM Iframe_Category ic WHERE ic.ativo = 1 and ic.iframe_category_id IN(SELECT DISTINCT(iframe_category_id) FROM Iframe_Category_Papel ifp WHERE ifp.papel_id = (SELECT papel_id FROM usuario u WHERE u.usuario_id = :usuarioId))", nativeQuery = true)
    List<IframeCategory> findAllAtivoByUsuarioId(@Param("usuarioId") Integer usuarioId);

}
