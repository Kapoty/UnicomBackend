package br.net.unicom.backend.security.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.net.unicom.backend.model.Permissao;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.repository.PermissaoRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  UsuarioRepository usuarioRepository;

  @Autowired
  PermissaoRepository permissaoRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Usuario usuario = usuarioRepository.findByEmailAndAtivo(email, true)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email or is not active: " + email));
    List<Permissao> permissoes = permissaoRepository.findAllByUsuarioId(usuario.getUsuarioId());

    return UserDetailsImpl.build(usuario, permissoes);
  }

}