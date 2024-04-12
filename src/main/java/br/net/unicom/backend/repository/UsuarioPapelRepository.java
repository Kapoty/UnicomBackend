package br.net.unicom.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.net.unicom.backend.model.UsuarioPapel;
import br.net.unicom.backend.model.key.UsuarioPapelKey;

public interface UsuarioPapelRepository extends JpaRepository<UsuarioPapel, UsuarioPapelKey> {

    
}
