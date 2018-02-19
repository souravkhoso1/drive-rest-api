import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.Gmail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Quickstart {
    /** Application name. */
    private static final String APPLICATION_NAME =
            "Gmail API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/gmail-java-quickstart");

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
     * at ~/.credentials/gmail-java-quickstart
     */
    private static final List<String> SCOPES =
            Arrays.asList(GmailScopes.GMAIL_LABELS,
                    GmailScopes.GMAIL_MODIFY,
                    GmailScopes.GMAIL_READONLY);

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
     * Build and return an authorized Gmail client service.
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public static Gmail getGmailService() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void mainMethod() throws IOException {
        // Build a new authorized API client service.
        Gmail service = getGmailService();
        Message msg;

        // Print the labels in the user's account.
        String user = "me";
        Pattern p = Pattern.compile("email\\s([a-zA-Z0-9\\.]+@gmail\\.com)");
        Matcher m;

        List<Message> labels = listMessagesMatchingQuery(service, user, "label:formspree");
        if (labels.size() == 0) {
            System.out.println("No labels found.");
        } else {
            System.out.println("Labels:");
            for (Message label : labels) {
                msg = getMessage(service, user, label.getId());
                m = p.matcher(msg.getSnippet().toString());
                System.out.printf("- %s\n", label.getId());
                while(m.find()){
                    System.out.println(m.group(1));
                }
                //System.out.printf("- %s\t%s\n", label.getId(), msg.getSnippet());
            }
        }
    }

    public static List<Message> listMessagesMatchingQuery(Gmail service, String userId,
                                                          String query) throws IOException {
        ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();

        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(userId).setQ(query)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }
        }

        for (Message message : messages) {
            System.out.println(message.toPrettyString());
        }

        return messages;
    }


    public static Message getMessage(Gmail service, String userId, String messageId)
            throws IOException {
        Message message = service.users().messages().get(userId, messageId).execute();

        //System.out.println("Message snippet: " + message.getSnippet());

        return message;
    }


}






//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.GenericUrl;
//import com.google.api.client.http.HttpResponse;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.GenericJson;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.util.store.FileDataStoreFactory;
//
//import com.google.api.services.drive.DriveScopes;
//import com.google.api.services.drive.model.*;
//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.model.File;
//
//import java.io.*;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//public class Quickstart {
//    /** Application name. */
//    private static final String APPLICATION_NAME =
//            "Drive API Java Quickstart";
//
//    /** Directory to store user credentials for this application. */
//    private static final java.io.File DATA_STORE_DIR = new java.io.File(
//            System.getProperty("user.home"), ".credentials/drive-java-quickstart");
//
//    /** Global instance of the {@link FileDataStoreFactory}. */
//    private static FileDataStoreFactory DATA_STORE_FACTORY;
//
//    /** Global instance of the JSON factory. */
//    private static final JsonFactory JSON_FACTORY =
//            JacksonFactory.getDefaultInstance();
//
//    /** Global instance of the HTTP transport. */
//    private static HttpTransport HTTP_TRANSPORT;
//
//    /** Global instance of the scopes required by this quickstart.
//     *
//     * If modifying these scopes, delete your previously saved credentials
//     * at ~/.credentials/drive-java-quickstart
//     */
//    private static final List<String> SCOPES =
//            Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY,
//                    DriveScopes.DRIVE_METADATA,
//                    DriveScopes.DRIVE_FILE,
//                    DriveScopes.DRIVE,
//                    DriveScopes.DRIVE_APPDATA,
//                    DriveScopes.DRIVE_PHOTOS_READONLY,
//                    DriveScopes.DRIVE_SCRIPTS);
//
//    static {
//        try {
//            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
//        } catch (Throwable t) {
//            t.printStackTrace();
//            System.exit(1);
//        }
//    }
//
//    /**
//     * Creates an authorized Credential object.
//     * @return an authorized Credential object.
//     * @throws IOException
//     */
//    public static Credential authorize() throws IOException {
//        // Load client secrets.
//        InputStream in =
//                Quickstart.class.getResourceAsStream("/client_secret.json");
//        GoogleClientSecrets clientSecrets =
//                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        // Build flow and trigger user authorization request.
//        GoogleAuthorizationCodeFlow flow =
//                new GoogleAuthorizationCodeFlow.Builder(
//                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                        .setDataStoreFactory(DATA_STORE_FACTORY)
//                        .setAccessType("offline")
//                        .build();
//        Credential credential = new AuthorizationCodeInstalledApp(
//                flow, new LocalServerReceiver()).authorize("user");
//        System.out.println(
//                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
//        return credential;
//    }
//
//    /**
//     * Build and return an authorized Drive client service.
//     * @return an authorized Drive client service
//     * @throws IOException
//     */
//    public static Drive getDriveService() throws IOException {
//        Credential credential = authorize();
//        return new Drive.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, credential)
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//    }
//
//    public static void main(String[] args) throws IOException {
//        // Build a new authorized API client service.
//        Drive service = getDriveService();
//
//        // https://drive.google.com/open?id=0BzyFMnstuAWRRFcwNDkxWXlfOXc
//        String fileId = "0BzyFMnstuAWRRFcwNDkxWXlfOXc";
//
//        /*File getMetadata = service.files().get(fileId).execute();
//        System.out.println(getMetadata.values());*/
//
//        About about = service.about().get().setFields("kind, storageQuota").execute();
//        System.out.println(about.getKind()+" "+about.getStorageQuota().toPrettyString());
//
//
//
////        // Print the names and IDs for up to 10 files.
////        FileList result = service.files().list()
////                //.setPageSize(10)
////                .setFields("nextPageToken, files(id, name, mimeType)")
////                .execute();
////        List<File> files = result.getFiles();
////        if (files == null || files.size() == 0) {
////            System.out.println("No files found.");
////        } else {
////            System.out.println("Files:");
////            for (File file : files) {
////                System.out.printf("%s\t%s\t%s\n", file.getName(), file.getId(), file.getMimeType());
////            }
////        }
//    }
//
//    /**
//     * Print a file's metadata.
//     *
//     * @param service Drive API service instance.
//     * @param fileId ID of the file to print metadata for.
//     */
////    private static void printFile(Drive service, String fileId) {
////
////        try {
////            File file = service.files().get(fileId).execute();
////
////            System.out.println("Title: " + file.getTitle());
////            System.out.println("Description: " + file.getDescription());
////            System.out.println("MIME type: " + file.getMimeType());
////        } catch (IOException e) {
////            System.out.println("An error occurred: " + e);
////        }
////    }
//
//    /**
//     * Download a file's content.
//     *
//     * @param service Drive API service instance.
//     * @param file Drive File instance.
//     * @return InputStream containing the file's content if successful,
//     *         {@code null} otherwise.
//     */
////    private static InputStream downloadFile(Drive service, File file) {
////        if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
////            try {
////                HttpResponse resp =
////                        service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()))
////                                .execute();
////                return resp.getContent();
////            } catch (IOException e) {
////                // An error occurred.
////                e.printStackTrace();
////                return null;
////            }
////        } else {
////            // The file doesn't have any content stored on Drive.
////            return null;
////        }
////    }
//
//}
