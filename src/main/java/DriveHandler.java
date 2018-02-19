import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.*;
import java.util.*;

public class DriveHandler {
    /** Application name. */
    private static final String APPLICATION_NAME =
            "Drive API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/drive-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart
     */
    private static final List<String> SCOPES =
            Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY,
                    DriveScopes.DRIVE_METADATA,
                    DriveScopes.DRIVE_FILE,
                    DriveScopes.DRIVE,
                    DriveScopes.DRIVE_APPDATA,
                    DriveScopes.DRIVE_PHOTOS_READONLY,
                    DriveScopes.DRIVE_SCRIPTS);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                Quickstart.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void givePermission(Set<String> allEmails) throws IOException {

        List<String> folderIds = new ArrayList<>();
        folderIds.add("0B6tlPl6Pw14fd2pKYzd6NWJSWGc");
        folderIds.add("0B6tlPl6Pw14femtkM0RjNnlYNVk");
        folderIds.add("0B6D_WdeSr-7Zbm1rc3NWUXRqNDQ");
        folderIds.add("0B6D_WdeSr-7ZeWhReTN3TUtvZ1k");
        folderIds.add("0B6D_WdeSr-7ZYkdMdTRLODYyWVE");
        folderIds.add("0B0EpbHcwk0ARdHAwMHhRODh1dkE");
        folderIds.add("0B0EpbHcwk0ARZjJPd09IZGQtMEk");
        folderIds.add("0B0EpbHcwk0ARUDRMSXNTT0t0LUE");
        folderIds.add("0BzYdwiqfr_0EZ0RzSXZrQ0kzR0U");

        /*for(String folderId: folderIds){
            System.out.println(folderId);
        }*/

        // Build a new authorized API client service.
        Drive service = getDriveService();

        //givePermission(service, "0B4qKF8worZ9WR2w2el9TbVZVNUE", "himymepisodes1@gmail.com");

        for(String emailId: allEmails){
            for(String folderId: folderIds){
                while(!givePermission(service, folderId, emailId)){
                    System.out.println("Permission not given for folderid: "+folderId);
                }
            }
            System.out.println("Given Permission to email: "+emailId);
        }



        /*File getMetadata = service.files().get(fileId).execute();
        System.out.println(getMetadata.values());*/

        //About about = service.about().get().setFields("kind, storageQuota").execute();
        //System.out.println(about.getKind()+" "+about.getStorageQuota().toPrettyString());

    }

    public boolean givePermission(Drive service, String file, String emailId)
            throws IOException {

        try {
            Permission p = service.permissions().create(file, new Permission().setType("user").setRole("reader").setEmailAddress(emailId)).execute();
        } catch (GoogleJsonResponseException e){
            //e.printStackTrace();
            return false;
        }
        return true;
    }




}
