package br.net.unicom.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.net.unicom.backend.model.Equipe;
import br.net.unicom.backend.model.FiltroVenda;
import br.net.unicom.backend.model.Papel;
import br.net.unicom.backend.model.Permissao;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.VendaVisao;
import br.net.unicom.backend.model.exception.EquipeInvalidaException;
import br.net.unicom.backend.model.exception.PapelInvalidoException;
import br.net.unicom.backend.model.exception.RegistroPontoUnauthorizedException;
import br.net.unicom.backend.model.exception.UsuarioEmailDuplicateException;
import br.net.unicom.backend.model.exception.UsuarioMatriculaDuplicateException;
import br.net.unicom.backend.payload.request.UsuarioPatchRequest;
import br.net.unicom.backend.payload.request.UsuarioPingRequest;
import br.net.unicom.backend.payload.request.UsuarioPostRequest;
import br.net.unicom.backend.payload.response.IframeCategoryResponse;
import br.net.unicom.backend.payload.response.UsuarioMeResponse;
import br.net.unicom.backend.repository.EquipeRepository;
import br.net.unicom.backend.repository.FiltroVendaRepository;
import br.net.unicom.backend.repository.IframeCategoryRepository;
import br.net.unicom.backend.repository.JornadaRepository;
import br.net.unicom.backend.repository.PapelRepository;
import br.net.unicom.backend.repository.PermissaoRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.repository.VendaVisaoRepository;
import br.net.unicom.backend.security.jwt.PontoJwtUtils;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.UploadService;
import br.net.unicom.backend.service.UsuarioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;



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
    PapelRepository papelRepository;

    @Autowired
    JornadaRepository jornadaRepository;

    @Autowired
    EquipeRepository equipeRepository;

    @Autowired
    FiltroVendaRepository filtroVendaRepository;

    @Autowired
    VendaVisaoRepository vendaVisaoRepository;

    @Autowired
    UploadService uploadService;
    
    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    PontoJwtUtils pontoJwtUtils;

    @PreAuthorize("hasAuthority('CADASTRAR_USUARIOS')")
    @GetMapping("/{usuarioId}")
    @JsonView(Usuario.DefaultView.class)
    public ResponseEntity<Usuario> getUsuarioByEmpresaIdAndUsuarioId(@Valid @PathVariable("usuarioId") Integer usuarioId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioIdAndEmpresaId(usuarioId, userDetails.getEmpresaId()).orElseThrow(NoSuchElementException::new);

        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/{usuarioId}/foto-perfil")
    public ResponseEntity<Resource> getUsuarioFotoPerfilByUsuarioId(@Valid @PathVariable("usuarioId") Integer usuarioId) {

        Usuario usuario = usuarioRepository.findByUsuarioId(usuarioId).orElseThrow(NoSuchElementException::new);

        if (!usuario.getFotoPerfil())
            return ResponseEntity.notFound().build();
        
        Resource file = uploadService.load(usuarioService.getUsuarioFotoPerfilFilename(usuario));
        return ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_JPEG)
                            .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                            .body(file);
    }

    @PreAuthorize("hasAuthority('CADASTRAR_USUARIOS')")
    @PostMapping("/{usuarioId}/foto-perfil")
    public ResponseEntity<Void> postUsuarioFotoPerfilByUsuarioIdandEmpresaId(@Valid @PathVariable("usuarioId") Integer usuarioId, @RequestParam("file") MultipartFile file) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioIdAndEmpresaId(usuarioId, userDetails.getEmpresaId()).orElseThrow(NoSuchElementException::new);

        String extension = uploadService.getFilenameExtension(file.getOriginalFilename());
        if (!extension.equals(".jpg"))
            return ResponseEntity.badRequest().build();
        
        try {
            uploadService.save(file, usuarioService.getUsuarioFotoPerfilFilename(usuario));

            usuario.setFotoPerfil(true);
            usuario.setFotoPerfilVersao(usuario.getFotoPerfilVersao() + 1);
            usuarioRepository.save(usuario);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @PreAuthorize("hasAuthority('CADASTRAR_USUARIOS')")
    @DeleteMapping("/{usuarioId}/foto-perfil")
    public ResponseEntity<Void> deleteUsuarioFotoPerfilByUsuarioIdandEmpresaId(@Valid @PathVariable("usuarioId") Integer usuarioId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioIdAndEmpresaId(usuarioId, userDetails.getEmpresaId()).orElseThrow(NoSuchElementException::new);

        try {
            uploadService.delete(usuarioService.getUsuarioFotoPerfilFilename(usuario));

            usuario.setFotoPerfil(false);
            usuarioRepository.save(usuario);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @PreAuthorize("hasAuthority('CADASTRAR_USUARIOS')")
    @GetMapping("/{usuarioId}/permissao")
    public ResponseEntity<List<Permissao>> getPermissaoListByEmpresaIdAndUsuarioId(@Valid @PathVariable("usuarioId") Integer usuarioId) {
        return ResponseEntity.ok(permissaoRepository.findAllByUsuarioId(usuarioId));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioMeResponse> getUsuarioByMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getId()).get();
        UsuarioMeResponse usuarioMeResponse = modelMapper.map(usuario, UsuarioMeResponse.class);
        usuarioMeResponse.setPermissaoList(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList()));
        return ResponseEntity.ok(usuarioMeResponse);
    }

    @PreAuthorize("hasAuthority('VER_MODULO_IFRAME')")
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

    @PreAuthorize("hasAuthority('VER_MODULO_MINHA_EQUIPE')")
    @GetMapping("/me/minha-equipe")
    public ResponseEntity<List<Equipe>> getMinhaEquipeListByMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        return ResponseEntity.ok(usuarioService.getMinhaEquipeListByUsuario(usuario));
    }

    @GetMapping("/me/usuario-list")
    @JsonView(Usuario.ExpandedView.class)
    public ResponseEntity<List<Usuario>> getUsuarioListLessThanMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        return ResponseEntity.ok(usuarioService.getUsuarioListLessThanUsuario(usuario));
    }

    @GetMapping("/me/papel-list")
    public ResponseEntity<List<Papel>> getPapelListLessThanMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        return ResponseEntity.ok(
            usuario.getPapel().getPapelMaiorQueList()
            .stream()
            .map(papelMaiorQue -> papelMaiorQue.getPapelFilho())
            .collect(Collectors.toList())
        );
    }

    @GetMapping("/me/filtro-venda")
    public ResponseEntity<FiltroVenda> getFiltroVendaByMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.of(filtroVendaRepository.findByUsuarioId(userDetails.getUsuarioId()));
    }

    @GetMapping("/me/venda-visao")
    public ResponseEntity<List<VendaVisao>> getVendaVisaoListByMe() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(vendaVisaoRepository.findAllByUsuarioId(userDetails.getUsuarioId()));
    }

    @PreAuthorize("hasAuthority('CADASTRAR_USUARIOS')")
    @PatchMapping("/{usuarioId}")
    @Transactional
    public ResponseEntity<Void> patchUsuarioByUsuarioId(@Valid @PathVariable Integer usuarioId, @Valid @RequestBody UsuarioPatchRequest patchUsuarioRequest) throws UsuarioEmailDuplicateException, UsuarioMatriculaDuplicateException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        logger.info(patchUsuarioRequest.toString());
        
        Usuario usuarioPai = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);
        Usuario usuarioFilho = usuarioRepository.findByUsuarioId(usuarioId).orElseThrow(NoSuchElementException::new);

        if (!usuarioService.isUsuarioGreaterThan(usuarioPai, usuarioFilho))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (patchUsuarioRequest.getEmail() != null) {
            Integer usuarioIdByEmail = usuarioRepository.getUsuarioIdByEmailAndEmpresaId(patchUsuarioRequest.getEmail().get(), usuarioFilho.getEmpresaId());
            if (usuarioIdByEmail == null || usuarioIdByEmail.equals(usuarioFilho.getUsuarioId()))
                usuarioFilho.setEmail(patchUsuarioRequest.getEmail().get());
            else throw new UsuarioEmailDuplicateException();
        }
        if (patchUsuarioRequest.getSenha() != null)
            usuarioFilho.setSenha(encoder.encode(patchUsuarioRequest.getSenha().get()));
        if (patchUsuarioRequest.getNome() != null)
            usuarioFilho.setNome(patchUsuarioRequest.getNome().get());
        if (patchUsuarioRequest.getNomeCompleto() != null)
            usuarioFilho.setNomeCompleto(patchUsuarioRequest.getNomeCompleto().get());
        if (patchUsuarioRequest.getAtivo() != null)
            usuarioFilho.setAtivo(patchUsuarioRequest.getAtivo().get());
        if (patchUsuarioRequest.getMatricula() != null) {
            if (patchUsuarioRequest.getMatricula().isEmpty())
                usuarioFilho.setMatricula(null);
            else {
                Integer usuarioIdByMatriculaAndEmpresaId = usuarioRepository.getUsuarioIdByMatriculaAndEmpresaId(patchUsuarioRequest.getMatricula().get(), usuarioFilho.getEmpresaId());
                if (usuarioIdByMatriculaAndEmpresaId == null || usuarioIdByMatriculaAndEmpresaId.equals(usuarioFilho.getUsuarioId()))
                    usuarioFilho.setMatricula(patchUsuarioRequest.getMatricula().orElse(null));
                else throw new UsuarioMatriculaDuplicateException();
            }
        }
        if (patchUsuarioRequest.getPapelId() != null) {
            Papel papel = papelRepository.findByPapelId(patchUsuarioRequest.getPapelId().get()).get();

            if (usuarioService.getPapelFilhoList(usuarioPai).contains(papel))
                usuarioFilho.setPapelId(patchUsuarioRequest.getPapelId().get());
        }
        if (patchUsuarioRequest.getDataNascimento() != null)
            usuarioFilho.setDataNascimento(patchUsuarioRequest.getDataNascimento().orElse(null));
        if (patchUsuarioRequest.getCpf() != null)
            usuarioFilho.setCpf(patchUsuarioRequest.getCpf().orElse(null));
        if (patchUsuarioRequest.getTelefoneCelular() != null)
            usuarioFilho.setTelefoneCelular(patchUsuarioRequest.getTelefoneCelular().orElse(null));
        if (patchUsuarioRequest.getWhatsapp() != null)
            usuarioFilho.setWhatsapp(patchUsuarioRequest.getWhatsapp().orElse(null));
        if (patchUsuarioRequest.getDataContratacao() != null)
            usuarioFilho.setDataContratacao(patchUsuarioRequest.getDataContratacao().orElse(null));
        if (patchUsuarioRequest.getCargoId() != null)
            usuarioFilho.setCargoId(patchUsuarioRequest.getCargoId().orElse(null));
        if (patchUsuarioRequest.getContratoId() != null)
            usuarioFilho.setContratoId(patchUsuarioRequest.getContratoId().orElse(null));
        if (patchUsuarioRequest.getDepartamentoId() != null)
            usuarioFilho.setDepartamentoId(patchUsuarioRequest.getDepartamentoId().orElse(null));
        if (patchUsuarioRequest.getEquipeId() != null) {
            if (patchUsuarioRequest.getEquipeId().orElse(null) == null)
                usuarioFilho.setEquipeId(null);
            else {
                Equipe equipe = equipeRepository.findByEquipeId(patchUsuarioRequest.getEquipeId().get()).get();
                
                if (usuarioService.getMinhaEquipeListByUsuario(usuarioPai).contains(equipe))
                    usuarioFilho.setEquipeId(patchUsuarioRequest.getEquipeId().get());
            }
        }
        if (patchUsuarioRequest.getJornadaStatusGrupoId() != null)
            usuarioFilho.setJornadaStatusGrupoId(patchUsuarioRequest.getJornadaStatusGrupoId().orElse(null));

        usuarioRepository.saveAndFlush(usuarioFilho);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('CADASTRAR_USUARIOS')")
    @PostMapping("/")
    @Transactional
    @JsonView(Usuario.DefaultView.class)
    public ResponseEntity<Usuario> postUsuario(@Valid @RequestBody UsuarioPostRequest postUsuarioRequest) throws UsuarioEmailDuplicateException, UsuarioMatriculaDuplicateException, PapelInvalidoException, EquipeInvalidaException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario usuarioPai = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).orElseThrow(NoSuchElementException::new);

        Usuario usuario = modelMapper.map(postUsuarioRequest, Usuario.class);

        usuario.setEmpresaId(userDetails.getEmpresaId());
        if (usuarioRepository.getUsuarioIdByEmailAndEmpresaId(postUsuarioRequest.getEmail(), usuario.getEmpresaId()) != null)
            throw new UsuarioEmailDuplicateException();
        usuario.setSenha(encoder.encode(postUsuarioRequest.getSenha()));
        if (usuarioRepository.getUsuarioIdByMatriculaAndEmpresaId(postUsuarioRequest.getMatricula(), usuario.getEmpresaId()) != null)
            throw new UsuarioMatriculaDuplicateException();

        Papel papel = papelRepository.findByPapelId(postUsuarioRequest.getPapelId()).get();

        if (!usuarioService.getPapelFilhoList(usuarioPai).contains(papel))
            throw new PapelInvalidoException();

        if (postUsuarioRequest.getEquipeId() != null) {
            Equipe equipe = equipeRepository.findByEquipeId(postUsuarioRequest.getEquipeId()).get();
                
            if (!usuarioService.getMinhaEquipeListByUsuario(usuarioPai).contains(equipe))
                throw new EquipeInvalidaException();
        }

        usuarioRepository.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
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
}
