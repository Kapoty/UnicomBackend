package br.net.unicom.backend.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
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
import br.net.unicom.backend.repository.EmpresaRepository;
import br.net.unicom.backend.security.service.UserDetailsImpl;
import br.net.unicom.backend.service.AnexoService;
import jakarta.servlet.http.HttpServletResponse;
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
    
    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @GetMapping("/venda/{vendaId}")
    public List<File> listAllByVendaId(@Valid @PathVariable("vendaId") Integer vendaId) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Empresa empresa = empresaRepository.findByEmpresaId(userDetails.getEmpresaId()).orElseThrow(NoSuchElementException::new);

        return anexoService.listAllFilesByVendaId(empresa, vendaId, userDetails.hasAuthority("VER_LIXEIRA"));
    }
    
    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @PostMapping("/venda/{vendaId}/upload")
    public ResponseEntity<String> uploadByVendaId(@Valid @PathVariable("vendaId") Integer vendaId, @RequestParam("file") MultipartFile file) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Empresa empresa = empresaRepository.findByEmpresaId(userDetails.getEmpresaId()).orElseThrow(NoSuchElementException::new);
        
        String fileId = anexoService.uploadByVendaId(empresa, vendaId, file);

        if(fileId == null) 
            return ResponseEntity.internalServerError().build();
        
        return ResponseEntity.ok(fileId);
    }

    @GetMapping("/download/{fileId}")
    public void downloadByFileId(@Valid @PathVariable("fileId") String fileId, HttpServletResponse response) throws IOException, GeneralSecurityException {
        anexoService.downloadByFileId(fileId, response);
    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS')")
    @PostMapping("/trash/{fileId}")
    public ResponseEntity<Void> trashByFileId(@Valid @PathVariable("fileId") String fileId) throws Exception {
        anexoService.trashByFileId(fileId);
        
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS') && hasAuthority('VER_LIXEIRA')")
    @PostMapping("/untrash/{fileId}")
    public ResponseEntity<Void> untrashByFileId(@Valid @PathVariable("fileId") String fileId) throws Exception {
        anexoService.untrashByFileId(fileId);
        
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('CADASTRAR_VENDAS') && hasAuthority('VER_LIXEIRA')")
    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<Void> deleteByFileId(@Valid @PathVariable("fileId") String fileId) throws Exception {
        anexoService.deleteByFileId(fileId);
        
        return ResponseEntity.noContent().build();
    }

}   
