package br.net.unicom.backend.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.services.drive.model.File;

import br.net.unicom.backend.model.Empresa;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AnexoService {

    @Autowired
    GoogleDriveService googleDriveService;

    Logger logger = LoggerFactory.getLogger(AnexoService.class);

    public List<File> getAllFiles(Empresa empresa, Boolean includeTrashed) throws IOException, GeneralSecurityException {
        return googleDriveService.listFolderContent(empresa.getGoogledriveFolderId(), includeTrashed);
    }

    public List<File> listAllFilesByVendaId(Empresa empresa, Integer vendaId, Boolean includeTrashed) throws Exception {
        String folderId = googleDriveService.getFolderId(empresa.getGoogledriveFolderId(), "venda/" + vendaId.toString());

        return googleDriveService.listFolderContent(folderId, includeTrashed);
    }

    public String uploadByVendaId(Empresa empresa, Integer vendaId, MultipartFile file) {
        return googleDriveService.uploadFile(file, empresa.getGoogledriveFolderId(), "venda/" + vendaId.toString());
    }

    public void downloadByFileId(String fileId, HttpServletResponse response) throws IOException, GeneralSecurityException {
        //googleDriveService.downloadFile(fileId, response);
        response.sendRedirect(googleDriveService.getWebViewLink(fileId));
    }

    public void trashByFileId(String fileId) throws Exception {
        googleDriveService.trashFile(fileId);
    }

    public void untrashByFileId(String fileId) throws Exception {
        googleDriveService.untrashFile(fileId);
    }

    public void deleteByFileId(String fileId) throws Exception {
        googleDriveService.deleteFile(fileId);
    }

}
