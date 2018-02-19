import java.io.IOException;
import java.util.Set;

public class MainClass {

    public static void main(String[] args) throws IOException {

        String fromDate = args[0];
        String toDate = args[1];

        GmailHandler gmailHandler = new GmailHandler();
        Set<String> allEmails = gmailHandler.mainMethod(fromDate, toDate);
        System.out.println("All emails Fetched!!");

        DriveHandler driveHandler = new DriveHandler();
        driveHandler.givePermission(allEmails);

        System.out.println("Work done!!");
    }




}
