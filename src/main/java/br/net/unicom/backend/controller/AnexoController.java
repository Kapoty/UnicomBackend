package br.net.unicom.backend.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.services.drive.model.File;

import br.net.unicom.backend.model.Empresa;
import br.net.unicom.backend.model.Usuario;
import br.net.unicom.backend.model.Venda;
import br.net.unicom.backend.model.VendaAtualizacao;
import br.net.unicom.backend.model.enums.VendaStatusCategoriaEnum;
import br.net.unicom.backend.repository.EmpresaRepository;
import br.net.unicom.backend.repository.UsuarioRepository;
import br.net.unicom.backend.repository.VendaRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.AnexoService;
import br.net.unicom.backend.service.VendaService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;




@RestController
@Validated
@RequestMapping(
    value = "/anexo",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
public class AnexoController {

    @Autowired
    AnexoService anexoService;
    
    @Autowired
    EmpresaRepository empresaRepository;

    @Autowired
    VendaRepository vendaRepository;

    @Autowired
    VendaService vendaService;

    @Autowired
    UsuarioRepository usuarioRepository;
    
    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @GetMapping("/venda/{vendaId}")
    public ResponseEntity<List<File>> listAllByVendaId(@Valid @PathVariable("vendaId") Integer vendaId) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).get();
        Empresa empresa = empresaRepository.findByEmpresaId(userDetails.getEmpresaId()).get();
        Venda venda = vendaRepository.findByVendaId(vendaId).get();

        // verificar se o usuário tem permissão para alterar a venda

        // pode ver a venda
        if (!userDetails.hasAuthority("VER_TODAS_VENDAS") && !vendaService.usuarioPodeVerVenda(usuario , venda))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(anexoService.listAllFilesByVendaId(empresa, vendaId, userDetails.hasAuthority("VER_LIXEIRA")));
    }
    
    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @PostMapping("/venda/{vendaId}/upload")
    @Transactional
    public ResponseEntity<File> uploadByVendaId(@Valid @PathVariable("vendaId") Integer vendaId, @RequestParam("file") MultipartFile file) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).get();
        Empresa empresa = empresaRepository.findByEmpresaId(userDetails.getEmpresaId()).get();
        Venda venda = vendaRepository.findByVendaId(vendaId).get();

        // verificar se o usuário tem permissão para alterar a venda

        // pode ver a venda
        if (!userDetails.hasAuthority("VER_TODAS_VENDAS") && !vendaService.usuarioPodeVerVenda(usuario , venda))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        // pode alterar venda com status em pos_venda
        if (!userDetails.hasAuthority("ALTERAR_AUDITOR") && venda.getStatus().getCategoria().equals(VendaStatusCategoriaEnum.POS_VENDA))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        
        File newFile = anexoService.uploadByVendaId(empresa, vendaId, file);

        if(newFile == null) 
            return ResponseEntity.internalServerError().build();

        // atualizar dataStatus e criar atualizacao

        vendaService.novaAtualizacao(usuario, venda, "Anexo adicionado: " + file.getOriginalFilename());
        
        return ResponseEntity.ok(newFile);
    }

    @GetMapping("/download/{fileId}")
    public void downloadByFileId(@Valid @PathVariable("fileId") String fileId, HttpServletResponse response) throws IOException, GeneralSecurityException {
        anexoService.downloadByFileId(fileId, response);
    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @PostMapping("/venda/{vendaId}/trash/{fileId}")
    public ResponseEntity<Void> trashByFileId(@Valid @PathVariable("vendaId") Integer vendaId, @Valid @PathVariable("fileId") String fileId) throws Exception {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).get();
        Venda venda = vendaRepository.findByVendaId(vendaId).get();

        // verificar se o usuário tem permissão para alterar a venda

        // pode ver a venda
        if (!userDetails.hasAuthority("VER_TODAS_VENDAS") && !vendaService.usuarioPodeVerVenda(usuario , venda))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        // pode alterar venda com status em pos_venda
        if (!userDetails.hasAuthority("ALTERAR_AUDITOR") && venda.getStatus().getCategoria().equals(VendaStatusCategoriaEnum.POS_VENDA))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        File file = anexoService.trashByFileId(fileId);
        
        // atualizar dataStatus e criar atualizacao

        vendaService.novaAtualizacao(usuario, venda, "Anexo excluído: " + file.getName());

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS') && hasAuthority('VER_LIXEIRA')")
    @PostMapping("/venda/{vendaId}/untrash/{fileId}")
    public ResponseEntity<Void> untrashByFileId(@Valid @PathVariable("vendaId") Integer vendaId, @Valid @PathVariable("fileId") String fileId) throws Exception {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).get();
        Venda venda = vendaRepository.findByVendaId(vendaId).get();

        // verificar se o usuário tem permissão para alterar a venda

        // pode ver a venda
        if (!userDetails.hasAuthority("VER_TODAS_VENDAS") && !vendaService.usuarioPodeVerVenda(usuario , venda))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        // pode alterar venda com status em pos_venda
        if (!userDetails.hasAuthority("ALTERAR_AUDITOR") && venda.getStatus().getCategoria().equals(VendaStatusCategoriaEnum.POS_VENDA))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        File file = anexoService.untrashByFileId(fileId);

         // atualizar dataStatus e criar atualizacao

         vendaService.novaAtualizacao(usuario, venda, "Anexo restaurado: " + file.getName());
        
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS') && hasAuthority('VER_LIXEIRA')")
    @DeleteMapping("/venda/{vendaId}/delete/{fileId}")
    public ResponseEntity<Void> deleteByFileId(@Valid @PathVariable("vendaId") Integer vendaId, @Valid @PathVariable("fileId") String fileId) throws Exception {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByUsuarioId(userDetails.getUsuarioId()).get();
        Venda venda = vendaRepository.findByVendaId(vendaId).get();

        // verificar se o usuário tem permissão para alterar a venda

        // pode ver a venda
        if (!userDetails.hasAuthority("VER_TODAS_VENDAS") && !vendaService.usuarioPodeVerVenda(usuario , venda))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        // pode alterar venda com status em pos_venda
        if (!userDetails.hasAuthority("ALTERAR_AUDITOR") && venda.getStatus().getCategoria().equals(VendaStatusCategoriaEnum.POS_VENDA))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        File file = anexoService.deleteByFileId(fileId);

         // atualizar dataStatus e criar atualizacao

         vendaService.novaAtualizacao(usuario, venda, "Anexo excluído definitivamente: " + file.getName());
        
        return ResponseEntity.noContent().build();
    }

}   
