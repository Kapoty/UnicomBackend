package br.net.unicom.backend.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Component
public class GoogleSheetsService {
 
    Logger logger = LoggerFactory.getLogger(GoogleSheetsService.class);

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final List<String> SCOPES = SheetsScopes.all().stream().toList();

    private static Resource CREDENTIALS_FILE;

    @Value("${unicom.backend.googledrive.credentialsFile}")
    public void setCredentialsFile(Resource credentialsFile) {
        GoogleSheetsService.CREDENTIALS_FILE = credentialsFile;
    }

    private static String APPLICATION_NAME;
    
    @Value("${unicom.backend.googledrive.applicationName}")
    public void setApplicationName(String applicationName) {
        GoogleSheetsService.APPLICATION_NAME = applicationName;
    }

	private static String USER;

	@Value("${unicom.backend.googledrive.user}")
    public void setUser(String user) {
        GoogleSheetsService.USER = user;
    }

    private static HttpCredentialsAdapter getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws FileNotFoundException, IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(CREDENTIALS_FILE.getInputStream())
			.createDelegated(USER)
            .createScoped(SCOPES);
        credentials.refreshIfExpired();
        return new HttpCredentialsAdapter(credentials);
    }

    public Sheets getInstance() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build();
        return service;
    }
}