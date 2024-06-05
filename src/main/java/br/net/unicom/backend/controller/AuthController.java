package br.net.unicom.backend.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.net.unicom.backend.payload.request.LoginRequest;
import br.net.unicom.backend.payload.response.JwtResponse;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.security.jwt.JwtUtils;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import jakarta.validation.Valid;


@RestController
@Validated
@RequestMapping(
    value = "/auth",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class AuthController {


    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        String login = loginRequest.getLogin();

        String usuarioId = (usuarioRepository.findUsuarioIdByLoginAndDominio(login, loginRequest.getDominio()).orElse(-1)).toString();

        Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(usuarioId, loginRequest.getSenha()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
        List<String> permissoes = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, 
                                userDetails.getId(), 
                                userDetails.getEmpresaId(),
                                userDetails.getEmail(), 
                                userDetails.isEnabled(),
                                permissoes));
    }

    /*@GetMapping("/encode")
    public ResponseEntity<String> getMethodName(@RequestParam("senha") String senha) {
        return ResponseEntity.ok(encoder.encode(senha));
    }*/

}
