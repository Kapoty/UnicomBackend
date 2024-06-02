package br.net.unicom.backend.scheduledtask;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.api.services.drive.model.File;

import br.net.unicom.backend.service.FileService;
import br.net.unicom.backend.service.GoogleDriveService;

@Component
public class BackupScheduledTasks {

    Logger logger = LoggerFactory.getLogger(BackupScheduledTasks.class);

    @Autowired
    GoogleDriveService googleDriveService;

    @Autowired
    FileService fileService;

    @Value("${backup.path}")
    private String backupPath;

    @Value("${unicom.backend.googledrive.systemFolderId}")
    private String systemFolderId;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
	public void sincronizarBackups() {
        try {

            logger.info("Sincronizando backups com o GoogleDrive...");

            List<Path> backupFiles = fileService.loadAll(Path.of(backupPath));

            String googleDriveBackupFolderId = googleDriveService.getFolderId(systemFolderId, "backup");

            List<File> backupFilesOnGoogleDrive = googleDriveService.listFolderContent(googleDriveBackupFolderId, false);
            List<String> backupFilesOnGoogleDriveNameList = backupFilesOnGoogleDrive.stream().map(file -> file.getName()).collect(Collectors.toList());

            backupFiles.forEach(backupFile -> {
                String filename = backupFile.getFileName().toString();
                if (!backupFilesOnGoogleDriveNameList.contains(filename)) {
                    try {
                        logger.info("Sincronizando backup: " + filename);
                        googleDriveService.uploadFile(fileService.load(backupFile), googleDriveBackupFolderId, "/");
                        logger.error("Sincronizado com sucesso");
                    } catch (Exception e) {
                        logger.error("Falha ao sincronizar!");
                    }
                    
                }
            });

            logger.info("Backups sincronizados!");

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}
