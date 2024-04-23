package br.net.unicom.backend.service;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.services.drive.model.File;

import br.net.unicom.backend.model.Empresa;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AnexoService {

    @Autowired
    GoogleDriveService googleDriveService;

    Logger logger = LoggerFactory.getLogger(AnexoService.class);

    public List<File> getAllFiles(Empresa empresa) throws IOException, GeneralSecurityException {
        return googleDriveService.listFolderContent(empresa.getGoogledriveFolderId());
    }

    public List<File> listAllFilesByVendaId(Empresa empresa, Integer vendaId) throws Exception {
        String folderId = googleDriveService.getFolderId(empresa.getGoogledriveFolderId(), "venda/" + vendaId.toString());

        return googleDriveService.listFolderContent(folderId);
    }

    public String uploadByVendaId(Empresa empresa, Integer vendaId, MultipartFile file) {
        return googleDriveService.uploadFile(file, empresa.getGoogledriveFolderId(), "venda/" + vendaId.toString());
    }

    public void downloadByFileId(String fileId, OutputStream outputStream) throws IOException, GeneralSecurityException {
        googleDriveService.downloadFile(fileId, outputStream);
    }

    public void deleteByFileId(String fileId) throws Exception {
        googleDriveService.deleteFile(fileId);
    }

}
