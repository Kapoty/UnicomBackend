package br.net.unicom.backend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import br.net.unicom.backend.model.Grupo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class GrupoRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GrupoRepository grupoRepository;

    @Test
    public void whenFindById_thenReturnGrupo() {
        Grupo grupo = new Grupo();
        grupo.setNome("Grupo");
        entityManager.persist(grupo);
        entityManager.flush();

        Optional<Grupo> found = grupoRepository.findByGrupoId(grupo.getGrupoId());

        assertEquals(grupo, found.get());
    }

}
