package br.net.unicom.backend.security.service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.net.unicom.backend.model.Permissao;
import br.net.unicom.backend.model.Usuario;

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;
  
    private Integer usuarioId;

    private Integer empresaId;
  
    private String email;

    private Boolean ativo;

    @JsonIgnore
    private String senha;
  
    private Collection<? extends GrantedAuthority> authorities;
  
    public UserDetailsImpl(Integer usuarioId, Integer empresaId, String email, String senha, Boolean ativo,
        Collection<? extends GrantedAuthority> authorities) {
      this.usuarioId = usuarioId;
      this.empresaId = empresaId;
      this.email = email;
      this.senha = senha;
      this.ativo = ativo;
      this.authorities = authorities;
    }
  
    public static UserDetailsImpl build(Usuario usuario, List<Permissao> permissoes) {
      List<GrantedAuthority> authorities = permissoes.stream()
          .map(permissao -> new SimpleGrantedAuthority(permissao.getNome()))
          .collect(Collectors.toList());
  
      return new UserDetailsImpl(
          usuario.getUsuarioId(), 
          usuario.getEmpresaId(),
          usuario.getEmail(),
          usuario.getSenha(), 
          usuario.getAtivo(),
          authorities);
    }
  
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return authorities;
    }

    public Boolean hasAuthority(String authority) {
      return this.getAuthorities().contains(new SimpleGrantedAuthority(authority));
    }

    public Integer getId() {
      return usuarioId;
    }

    public Integer getUsuarioId() {
      return usuarioId;
    }
  
    public String getEmail() {
      return email;
    }

    public Integer getEmpresaId() {
      return empresaId;
    }
  
    @Override
    public String getPassword() {
      return senha;
    }
  
    @Override
    public String getUsername() {
      return usuarioId.toString();
    }
  
    @Override
    public boolean isAccountNonExpired() {
      return true;
    }
  
    @Override
    public boolean isAccountNonLocked() {
      return true;
    }
  
    @Override
    public boolean isCredentialsNonExpired() {
      return true;
    }
  
    @Override
    public boolean isEnabled() {
      return ativo;
    }
  
    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      UserDetailsImpl usuario = (UserDetailsImpl) o;
      return Objects.equals(usuarioId, usuario.usuarioId);
    }
  }
