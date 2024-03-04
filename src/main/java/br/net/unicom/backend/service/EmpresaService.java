package br.net.unicom.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.net.unicom.backend.model.Empresa;
import br.net.unicom.backend.model.Papel;
import br.net.unicom.backend.model.Permissao;
import br.net.unicom.backend.repository.EmpresaRepository;
import br.net.unicom.backend.repository.PapelRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class EmpresaService {

    Logger logger = LoggerFactory.getLogger(EmpresaService.class);

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private PapelRepository papelRepository;

    public Optional<List<Permissao>> getPermissoesByEmpresaId(Integer empresaId) {
        Optional<Empresa> empresa = empresaRepository.findByEmpresaId(empresaId);
        List<Permissao> permissoes = new ArrayList<>();
        if (empresa.isPresent()) {
            empresa.get().getEmpresaPermissoes().forEach((p) -> permissoes.add(p.getPermissao()));
            return Optional.of(permissoes);
        }
        return Optional.empty();
    }
    
}
