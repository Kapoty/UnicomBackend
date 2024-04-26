package br.net.unicom.backend.service;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class GoogleDriveService {
 
    Logger logger = LoggerFactory.getLogger(GoogleDriveService.class);

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final List<String> SCOPES = DriveScopes.all().stream().toList();

    private static Resource CREDENTIALS_FILE;

    @Value("${unicom.backend.googledrive.credentialsFile}")
    public void setCredentialsFile(Resource credentialsFile) {
        GoogleDriveService.CREDENTIALS_FILE = credentialsFile;
    } 

    private static String APPLICATION_NAME;
    
    @Value("${unicom.backend.googledrive.applicationName}")
    public void setApplicationName(String applicationName) {
        GoogleDriveService.APPLICATION_NAME = applicationName;
    } 

    private static HttpCredentialsAdapter getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws FileNotFoundException, IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(CREDENTIALS_FILE.getInputStream())
            .createScoped(SCOPES);
        credentials.refreshIfExpired();
        return new HttpCredentialsAdapter(credentials);
    }

    public Drive getInstance() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build();
        return service;
    }

    public List<File> listEverything() throws IOException, GeneralSecurityException {
		// Print the names and IDs for up to 10 files.
		FileList result = getInstance().files().list()
				.setPageSize(10)
				.setFields("nextPageToken, files(id, name)")
				.execute();
		return result.getFiles();
	}

	public List<File> listFolderContent(String parentId) throws IOException, GeneralSecurityException {
		if (parentId == null) {
			parentId = "root";
		}
		String query = "'" + parentId + "' in parents";
		FileList result = getInstance().files().list()
				.setQ(query)
				.setPageSize(100)
				.setFields("nextPageToken, files(id, name)")
				.execute();
		return result.getFiles();
	}

	public void downloadFile(String fileId, HttpServletResponse response) throws IOException, GeneralSecurityException {
		File file = getInstance().files().get(fileId).execute();
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
		response.setContentType(file.getMimeType());
		getInstance().files().get(fileId).executeMediaAndDownloadTo(response.getOutputStream());
	}

	public void trashFile(String fileId) throws Exception {
		File newContent = new File();
		newContent.setTrashed(true);
		getInstance().files().update(fileId, newContent).setSupportsAllDrives(true).execute();
	}

	public void deleteFile(String fileId) throws Exception {
		getInstance().files().delete(fileId).setSupportsAllDrives(true).execute();
	}

	public String uploadFile(MultipartFile file, String parentId, String filePath) {
		try {
			String folderId = getFolderId(parentId, filePath);
			if (null != file) {
				File fileMetadata = new File();
				fileMetadata.setParents(Collections.singletonList(folderId));
				fileMetadata.setName(file.getOriginalFilename());
				File uploadFile = getInstance()
						.files()
						.create(fileMetadata, new InputStreamContent(
								file.getContentType(),
								new ByteArrayInputStream(file.getBytes()))
						)
						.setFields("id").execute();
				return uploadFile.getId();
			}
		} catch (Exception e) {
			logger.error("Error: ", e);
		}
		return null;
	}

	public String getFolderId(String parentId, String path) throws Exception {
		String[] folderNames = path.split("/");

		Drive driveInstance = getInstance();
		for (String name : folderNames) {
			parentId = findOrCreateFolder(parentId, name, driveInstance);
		}
		return parentId;
	}

	private String findOrCreateFolder(String parentId, String folderName, Drive driveInstance) throws Exception {
		String folderId = searchFolderId(parentId, folderName, driveInstance);
		// Folder already exists, so return id
		if (folderId != null) {
			return folderId;
		}
		//Folder dont exists, create it and return folderId
		File fileMetadata = new File();
		fileMetadata.setMimeType("application/vnd.google-apps.folder");
		fileMetadata.setName(folderName);

		if (parentId != null) {
			fileMetadata.setParents(Collections.singletonList(parentId));
		}
		return driveInstance.files().create(fileMetadata)
				.setFields("id")
				.execute()
				.getId();
	}

	private String searchFolderId(String parentId, String folderName, Drive service) throws Exception {
		String folderId = null;
		String pageToken = null;
		FileList result = null;

		File fileMetadata = new File();
		fileMetadata.setMimeType("application/vnd.google-apps.folder");
		fileMetadata.setName(folderName);

		do {
			String query = " mimeType = 'application/vnd.google-apps.folder' ";
			if (parentId == null) {
				query = query + " and 'root' in parents";
			} else {
				query = query + " and '" + parentId + "' in parents";
			}
			result = service.files().list().setQ(query)
					.setSpaces("drive")
					.setFields("nextPageToken, files(id, name)")
					.setPageToken(pageToken)
					.execute();

			for (File file : result.getFiles()) {
				if (file.getName().equalsIgnoreCase(folderName)) {
					folderId = file.getId();
				}
			}
			pageToken = result.getNextPageToken();
		} while (pageToken != null && folderId == null);

		return folderId;
	}

    /*public List<File> getFilesByPath(String googledriveFolderId) throws IOException, GeneralSecurityException {

        Drive service = getInstance();

        FileList result = service.files().list()
            .setPageSize(100)
            .setSupportsAllDrives(true)
            .setIncludeItemsFromAllDrives(true)
            .setQ(String.format("'%s' in parents", googledriveFolderId))
            .setFields("nextPageToken, files(id, name)")
            .execute();

        List<File> files = result.getFiles();

        return files;
    }*/

}