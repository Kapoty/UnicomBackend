package br.net.unicom.backend.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EquipeService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UsuarioService usuarioService;

}
