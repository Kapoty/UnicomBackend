package br.net.unicom.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.net.unicom.backend.repository.EmpresaRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class EmpresaService {

    Logger logger = LoggerFactory.getLogger(EmpresaService.class);

    @Autowired
    private EmpresaRepository empresaRepository;
    
}
