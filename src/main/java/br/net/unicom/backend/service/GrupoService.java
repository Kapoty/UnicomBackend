package br.net.unicom.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.net.unicom.backend.model.Empresa;
import br.net.unicom.backend.model.Grupo;
import br.net.unicom.backend.repository.GrupoRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    public Optional<List<Empresa>> getEmpresasByGrupoId(Integer grupoId) {
        Optional<Grupo> grupo = grupoRepository.findByGrupoId(grupoId);
        Optional<List<Empresa>> empresas = Optional.empty();
        if (grupo.isPresent())
            empresas = Optional.of(grupo.get().getEmpresaList());
        return empresas;
    }

}
