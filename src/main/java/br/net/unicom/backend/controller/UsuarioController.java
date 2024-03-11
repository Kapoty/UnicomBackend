package br.net.unicom.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.net.unicom.backend.model.Permissao;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.UsuarioEmailDuplicateException;
import br.net.unicom.backend.model.UsuarioMatriculaDuplicateException;
import br.net.unicom.backend.payload.request.PatchUsuarioRequest;
import br.net.unicom.backend.payload.request.PostUsuarioRequest;
import br.net.unicom.backend.payload.response.IframeCategoryResponse;
import br.net.unicom.backend.payload.response.UsuarioMeResponse;
import br.net.unicom.backend.repository.IframeCategoryRepository;
import br.net.unicom.backend.repository.PermissaoRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.Validator;



@RestController
@Validated
@RequestMapping(
    value = "/usuario",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class UsuarioController {
    
    Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PermissaoRepository permissaoRepository;

    @Autowired
    IframeCategoryRepository iframeCategoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
	private Validator validator;

    @PreAuthorize("hasAuthority('Admin.Usuario.Read.All')")
    @GetMapping("")
    public ResponseEntity<List<Usuario>> getAll() {
        return new ResponseEntity<List<Usuario>>(usuarioRepository.findAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('Admin.Usuario.Read.All')")
    @GetMapping("/{usuarioId}")
    public ResponseEntity<Usuario> getUsuarioByEmpresaIdAndUsuarioId(@Valid @PathVariable("usuarioId") Integer usuarioId) {
        return ResponseEntity.of(usuarioRepository.findByUsuarioId(usuarioId));
    }

    @PreAuthorize("hasAuthority('Admin.Usuario.Read.All')")
    @GetMapping("/{usuarioId}/permissao")
    public ResponseEntity<List<Permissao>> getPermissaoListByEmpresaIdAndUsuarioId(@Valid @PathVariable("usuarioId") Integer usuarioId) {
        return ResponseEntity.ok(permissaoRepository.findAllByUsuarioId(usuarioId));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioMeResponse> getUsuarioByMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getId()).get();
        return ResponseEntity.ok(new UsuarioMeResponse(
            usuario.getUsuarioId(),
            usuario.getEmail(),
            usuario.getNome(),
            usuario.getAtivo(),
            usuario.getMatricula(),
            usuario.getEmpresaId(),
            usuario.getEmpresa(),
            usuario.getUsuarioPapelList().stream().map(up -> up.getPapel()).collect(Collectors.toList()),
            SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList())
        ));
    }

    @PreAuthorize("hasAuthority('Iframe.Read.All')")
    @GetMapping("/me/iframe-category")
    public ResponseEntity<List<IframeCategoryResponse>> getIframeCategoryListByMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<IframeCategoryResponse> iframeCategoryResponsesList = new ArrayList<>();
        iframeCategoryRepository.findAllAtivoByUsuarioId(userDetails.getId()).forEach(iframeCategory -> iframeCategoryResponsesList.add(new IframeCategoryResponse(
            iframeCategory.getIframeCategoryId(),
            iframeCategory.getTitulo(),
            iframeCategory.getUri(),
            iframeCategory.getIcon(),
            iframeCategory.getAtivo(),
            iframeCategory.getEmpresaId(),
            iframeCategory.getIframeList().stream().filter((iframe) -> iframe.getAtivo()).collect(Collectors.toList())
        )));
        return ResponseEntity.ok(iframeCategoryResponsesList);
    }

    @PreAuthorize("hasAuthority('Usuario.Write.All')")
    @PatchMapping("/{usuarioId}")
    @Transactional
    public ResponseEntity<Void> patchUsuarioByUsuarioId(@Valid @PathVariable Integer usuarioId, @Valid @RequestBody PatchUsuarioRequest patchUsuarioRequest) throws UsuarioEmailDuplicateException, UsuarioMatriculaDuplicateException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario = usuarioRepository.findByUsuarioIdAndEmpresaId(usuarioId, userDetails.getEmpresaId()).orElseThrow(NoSuchElementException::new);

        if (patchUsuarioRequest.getEmail() != null) {
            Integer usuarioIdByEmail = usuarioRepository.getUsuarioIdByEmail(patchUsuarioRequest.getEmail().get());
            if (usuarioIdByEmail == null || usuarioIdByEmail== usuario.getUsuarioId())
                usuario.setEmail(patchUsuarioRequest.getEmail().get());
            else throw new UsuarioEmailDuplicateException();
        }
        if (patchUsuarioRequest.getSenha() != null)
            usuario.setSenha(encoder.encode(patchUsuarioRequest.getSenha().get()));
        if (patchUsuarioRequest.getNome() != null)
            usuario.setNome(patchUsuarioRequest.getNome().get());
        if (patchUsuarioRequest.getAtivo() != null)
            usuario.setAtivo(patchUsuarioRequest.getAtivo().get());
        if (patchUsuarioRequest.getMatricula() != null) {
            Integer usuarioIdByByMatriculaAndEmpresaId = usuarioRepository.getUsuarioIdByMatriculaAndEmpresaId(patchUsuarioRequest.getMatricula().get(), usuario.getEmpresaId());
            if (usuarioIdByByMatriculaAndEmpresaId == null || usuarioIdByByMatriculaAndEmpresaId == usuario.getUsuarioId())
                usuario.setMatricula(patchUsuarioRequest.getMatricula().orElse(null));
            else throw new UsuarioMatriculaDuplicateException();
        }

        usuarioRepository.save(usuario);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Usuario.Write.All')")
    @PostMapping("/{usuarioId}")
    @Transactional
    public ResponseEntity<Usuario> postUsuarioByUsuarioId(@Valid @PathVariable Integer usuarioId, @Valid @RequestBody PostUsuarioRequest postUsuarioRequest) throws UsuarioEmailDuplicateException, UsuarioMatriculaDuplicateException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario = modelMapper.map(postUsuarioRequest, Usuario.class);

        usuario.setEmpresaId(userDetails.getEmpresaId());
        if (usuarioRepository.getUsuarioIdByEmail(postUsuarioRequest.getEmail()) != null)
            throw new UsuarioEmailDuplicateException();
        usuario.setSenha(encoder.encode(postUsuarioRequest.getSenha()));
        if (usuarioRepository.getUsuarioIdByMatriculaAndEmpresaId(postUsuarioRequest.getMatricula(), usuario.getEmpresaId()) != null)
            throw new UsuarioMatriculaDuplicateException();

        usuario = usuarioRepository.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

}
