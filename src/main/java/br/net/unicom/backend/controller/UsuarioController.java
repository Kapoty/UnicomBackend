package br.net.unicom.backend.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.net.unicom.backend.model.Equipe;
import br.net.unicom.backend.model.Jornada;
import br.net.unicom.backend.model.Permissao;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.UsuarioPapel;
import br.net.unicom.backend.model.exception.RegistroPontoUnauthorizedException;
import br.net.unicom.backend.model.exception.UsuarioEmailDuplicateException;
import br.net.unicom.backend.model.exception.UsuarioMatriculaDuplicateException;
import br.net.unicom.backend.payload.request.PatchJornadaRequest;
import br.net.unicom.backend.payload.request.PatchUsuarioRequest;
import br.net.unicom.backend.payload.request.PostUsuarioRequest;
import br.net.unicom.backend.payload.request.UsuarioPingRequest;
import br.net.unicom.backend.payload.response.IframeCategoryResponse;
import br.net.unicom.backend.payload.response.UsuarioMeResponse;
import br.net.unicom.backend.payload.response.UsuarioResponse;
import br.net.unicom.backend.repository.EquipeRepository;
import br.net.unicom.backend.repository.IframeCategoryRepository;
import br.net.unicom.backend.repository.JornadaExcecaoRepository;
import br.net.unicom.backend.repository.JornadaRepository;
import br.net.unicom.backend.repository.PapelRepository;
import br.net.unicom.backend.repository.PermissaoRepository;
import br.net.unicom.backend.repository.UsuarioPapelRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.security.jwt.PontoJwtUtils;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.FileService;
import br.net.unicom.backend.service.UsuarioService;
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
    UsuarioPapelRepository usuarioPapelRepository;

    @Autowired
    PapelRepository papelRepository;

    @Autowired
    JornadaRepository jornadaRepository;

    @Autowired
    EquipeRepository equipeRepository;

    @Autowired
    JornadaExcecaoRepository jornadaExcecaoRepository;

    @Autowired
    FileService fileService;
    
    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
	private Validator validator;

    @Autowired
    PontoJwtUtils pontoJwtUtils;

    @PreAuthorize("hasAuthority('Usuario.Read.All')")
    @GetMapping("")
    public ResponseEntity<List<Usuario>> getAll() {
        return new ResponseEntity<List<Usuario>>(usuarioRepository.findAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('Usuario.Read.All')")
    @GetMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponse> getUsuarioByEmpresaIdAndUsuarioId(@Valid @PathVariable("usuarioId") Integer usuarioId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioIdAndEmpresaId(usuarioId, userDetails.getEmpresaId()).orElseThrow(NoSuchElementException::new);
        UsuarioResponse usuarioResponse = usuarioService.usuarioToUsuarioResponse(usuario);
        return ResponseEntity.ok(usuarioResponse);
    }

    @PreAuthorize("hasAuthority('Usuario.Read.All')")
    @GetMapping("/me/foto-perfil")
    public ResponseEntity<Resource> getUsuarioFotoPerfilByMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getId()).orElseThrow(NoSuchElementException::new);

        if (!usuario.getFotoPerfil())
            return ResponseEntity.notFound().build();
        
        Resource file = fileService.load(usuarioService.getUsuarioFotoPerfilFilename(usuario));
        return ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_JPEG)
                            .body(file);
    }

    @GetMapping("/{usuarioId}/foto-perfil")
    public ResponseEntity<Resource> getUsuarioFotoPerfilByUsuarioId(@Valid @PathVariable("usuarioId") Integer usuarioId) {
        Usuario usuario = usuarioRepository.findByUsuarioId(usuarioId).orElseThrow(NoSuchElementException::new);

        if (!usuario.getFotoPerfil())
            return ResponseEntity.notFound().build();
        
        Resource file = fileService.load(usuarioService.getUsuarioFotoPerfilFilename(usuario));
        return ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_JPEG)
                            .body(file);
    }

    @PreAuthorize("hasAuthority('Usuario.Write.All')")
    @PostMapping("/{usuarioId}/foto-perfil")
    public ResponseEntity<Void> postUsuarioFotoPerfilByUsuarioIdandEmpresaId(@Valid @PathVariable("usuarioId") Integer usuarioId, @RequestParam("file") MultipartFile file) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioIdAndEmpresaId(usuarioId, userDetails.getEmpresaId()).orElseThrow(NoSuchElementException::new);

        String extension = fileService.getFilenameExtension(file.getOriginalFilename());
        if (!extension.equals(".jpg"))
            return ResponseEntity.badRequest().build();
        
        try {
            fileService.save(file, usuarioService.getUsuarioFotoPerfilFilename(usuario));

            usuario.setFotoPerfil(true);
            usuario.setFotoPerfilVersao(usuario.getFotoPerfilVersao() + 1);
            usuarioRepository.save(usuario);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @PreAuthorize("hasAuthority('Usuario.Write.All')")
    @DeleteMapping("/{usuarioId}/foto-perfil")
    public ResponseEntity<Void> deleteUsuarioFotoPerfilByUsuarioIdandEmpresaId(@Valid @PathVariable("usuarioId") Integer usuarioId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioIdAndEmpresaId(usuarioId, userDetails.getEmpresaId()).orElseThrow(NoSuchElementException::new);

        try {
            fileService.delete(usuarioService.getUsuarioFotoPerfilFilename(usuario));

            usuario.setFotoPerfil(false);
            usuarioRepository.save(usuario);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @PreAuthorize("hasAuthority('Usuario.Read.All')")
    @GetMapping("/{usuarioId}/permissao")
    public ResponseEntity<List<Permissao>> getPermissaoListByEmpresaIdAndUsuarioId(@Valid @PathVariable("usuarioId") Integer usuarioId) {
        return ResponseEntity.ok(permissaoRepository.findAllByUsuarioId(usuarioId));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioMeResponse> getUsuarioByMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getId()).get();
        UsuarioMeResponse usuarioMeResponse = modelMapper.map(usuario, UsuarioMeResponse.class);
        usuarioMeResponse.setPapelList(papelRepository.findAllByUsuarioId(usuario.getUsuarioId()));
        usuarioMeResponse.setPermissaoList(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList()));
        return ResponseEntity.ok(usuarioMeResponse);
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

    @PreAuthorize("hasAuthority('MinhaEquipe.Read.All')")
    @GetMapping("/me/minha-equipe")
    public ResponseEntity<List<Equipe>> getMinhaEquipeListByMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails.hasAuthority("MinhaEquipe.Write.All"))
            return ResponseEntity.ok(equipeRepository.findAllByEmpresaId(userDetails.getEmpresaId()));

        return ResponseEntity.ok(equipeRepository.findAllBySupervisorId(userDetails.getId()));
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
            if (patchUsuarioRequest.getMatricula().isEmpty())
                usuario.setMatricula(null);
            else {
                Integer usuarioIdByMatriculaAndEmpresaId = usuarioRepository.getUsuarioIdByMatriculaAndEmpresaId(patchUsuarioRequest.getMatricula().get(), usuario.getEmpresaId());
                if (usuarioIdByMatriculaAndEmpresaId == null || usuarioIdByMatriculaAndEmpresaId == usuario.getUsuarioId())
                    usuario.setMatricula(patchUsuarioRequest.getMatricula().orElse(null));
                else throw new UsuarioMatriculaDuplicateException();
            }
        }
        if (patchUsuarioRequest.getPapelIdList() != null) {

            for(UsuarioPapel usuarioPapel : usuario.getUsuarioPapelList()) {
                if (!patchUsuarioRequest.getPapelIdList().get().contains(usuarioPapel.getPapel().getPapelId()))
                    usuarioPapelRepository.delete(usuarioPapel);
            }

            List<UsuarioPapel> usuarioAddPapelList = new ArrayList<>();

            for(Integer papelId : patchUsuarioRequest.getPapelIdList().get()) {
                usuarioAddPapelList.add(new UsuarioPapel(usuario, papelRepository.findByPapelId(papelId).orElseThrow(NoSuchElementException::new)));
            }
    
            usuario.setUsuarioPapelList(usuarioAddPapelList);
            usuarioPapelRepository.saveAllAndFlush(usuarioAddPapelList);
        }
        if (patchUsuarioRequest.getDataNascimento() != null)
        usuario.setDataNascimento(patchUsuarioRequest.getDataNascimento().orElse(null));
        if (patchUsuarioRequest.getCpf() != null)
        usuario.setCpf(patchUsuarioRequest.getCpf().orElse(null));
        if (patchUsuarioRequest.getTelefoneCelular() != null)
        usuario.setTelefoneCelular(patchUsuarioRequest.getTelefoneCelular().orElse(null));
        if (patchUsuarioRequest.getWhatsapp() != null)
        usuario.setWhatsapp(patchUsuarioRequest.getWhatsapp().orElse(null));
        if (patchUsuarioRequest.getDataContratacao() != null)
        usuario.setDataContratacao(patchUsuarioRequest.getDataContratacao().orElse(null));
        if (patchUsuarioRequest.getCargoId() != null)
        usuario.setCargoId(patchUsuarioRequest.getCargoId().orElse(null));
        if (patchUsuarioRequest.getContratoId() != null)
        usuario.setContratoId(patchUsuarioRequest.getContratoId().orElse(null));
        if (patchUsuarioRequest.getDepartamentoId() != null)
        usuario.setDepartamentoId(patchUsuarioRequest.getDepartamentoId().orElse(null));
        if (patchUsuarioRequest.getEquipeId() != null)
        usuario.setEquipeId(patchUsuarioRequest.getEquipeId().orElse(null));
        if (patchUsuarioRequest.getJornada() != null) {
            Jornada jornada = usuario.getJornada();
            if (patchUsuarioRequest.getJornada().orElse(null) == null) {
                if (jornada != null)
                    jornadaRepository.delete(jornada);
            }
            else {
                if (jornada == null) {
                    jornada = new Jornada();
                    jornada.setUsuario(usuario);
                }
                modelMapper.map(patchUsuarioRequest.getJornada().get(), jornada);
                jornadaRepository.saveAndFlush(jornada);
            }
        }

        usuario = usuarioRepository.saveAndFlush(usuario);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Usuario.Write.All')")
    @PostMapping("/")
    @Transactional
    public ResponseEntity<UsuarioResponse> postUsuarioByUsuarioId(@Valid @RequestBody PostUsuarioRequest postUsuarioRequest) throws UsuarioEmailDuplicateException, UsuarioMatriculaDuplicateException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuario = modelMapper.map(postUsuarioRequest, Usuario.class);

        usuario.setEmpresaId(userDetails.getEmpresaId());
        if (usuarioRepository.getUsuarioIdByEmail(postUsuarioRequest.getEmail()) != null)
            throw new UsuarioEmailDuplicateException();
        usuario.setSenha(encoder.encode(postUsuarioRequest.getSenha()));
        if (usuarioRepository.getUsuarioIdByMatriculaAndEmpresaId(postUsuarioRequest.getMatricula(), usuario.getEmpresaId()) != null)
            throw new UsuarioMatriculaDuplicateException();

        usuario = usuarioRepository.saveAndFlush(usuario);

        List<UsuarioPapel> usuarioPapelList = new ArrayList<>();

        for(Integer papelId : postUsuarioRequest.getPapelIdList()) {
            usuarioPapelList.add(new UsuarioPapel(usuario, papelRepository.findByPapelId(papelId).orElseThrow(NoSuchElementException::new)));
        }

        usuarioPapelRepository.saveAllAndFlush(usuarioPapelList);

        usuario.setUsuarioPapelList(usuarioPapelList);

        if (postUsuarioRequest.getJornada() != null) {
            Jornada jornada = new Jornada();
            jornada.setUsuario(usuario);
            modelMapper.map(postUsuarioRequest.getJornada(), jornada);

            usuario.setJornada(jornada);

            jornadaRepository.saveAndFlush(jornada);
        }

        usuario = usuarioRepository.saveAndFlush(usuario);

        UsuarioResponse usuarioResponse = usuarioService.usuarioToUsuarioResponse(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioResponse);
    }

    @PostMapping("/me/ping")
    public ResponseEntity<Void> usuarioPing(@Valid @RequestBody UsuarioPingRequest usuarioPingRequest) throws RegistroPontoUnauthorizedException {
        if (!pontoJwtUtils.validateJwtToken(usuarioPingRequest.getToken()))
            throw new RegistroPontoUnauthorizedException();

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getId()).get();
        usuarioService.ping(usuario);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MinhaEquipe.Read.All')")
    @GetMapping("/{usuarioId}/jornada")
    public ResponseEntity<Jornada> getJornadaByUsuarioId(@Valid @PathVariable("usuarioId") Integer usuarioId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(usuarioId).orElseThrow(NoSuchElementException::new);

        if (!usuarioService.isUsuarioSupervisorOf(userDetails.getId(), usuario) && !userDetails.hasAuthority("MinhaEquipe.Write.All"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(usuario.getJornada());
    }

    @PreAuthorize("hasAuthority('MinhaEquipe.Read.All')")
    @PatchMapping("/{usuarioId}/jornada")
    @Transactional
    public ResponseEntity<Jornada> patchJornadaByUsuarioId(@Valid @PathVariable("usuarioId") Integer usuarioId, @Valid @RequestBody PatchJornadaRequest patchJornadaRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(usuarioId).orElseThrow(NoSuchElementException::new);

        if (!usuarioService.isUsuarioSupervisorOf(userDetails.getId(), usuario) && !userDetails.hasAuthority("MinhaEquipe.Write.All"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (patchJornadaRequest.getJornada() != null) {
            Jornada jornada = usuario.getJornada();
            if (patchJornadaRequest.getJornada().orElse(null) == null) {
                if (jornada != null)
                    jornadaRepository.delete(jornada);
            }
            else {
                if (jornada == null) {
                    jornada = new Jornada();
                    jornada.setUsuario(usuario);
                }
                modelMapper.map(patchJornadaRequest.getJornada().get(), jornada);
                jornadaRepository.saveAndFlush(jornada);
            }
        }

        usuario = usuarioRepository.saveAndFlush(usuario);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MinhaEquipe.Read.All')")
    @GetMapping("/{usuarioId}/jornada-excecao-data-list")
    public ResponseEntity<List<LocalDate>> getJornadaExcecaoDataListByUsuarioId(@Valid @PathVariable("usuarioId") Integer usuarioId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(usuarioId).orElseThrow(NoSuchElementException::new);

        if (!usuarioService.isUsuarioSupervisorOf(userDetails.getId(), usuario) && !userDetails.hasAuthority("MinhaEquipe.Write.All"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(
            jornadaExcecaoRepository.getDataListByUsuarioId(usuario.getUsuarioId())
            .stream()
            .map((data) -> data.getData())
            .collect(Collectors.toList())
            );
    }
    
}
