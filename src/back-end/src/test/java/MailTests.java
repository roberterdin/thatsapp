import com.whatistics.backend.mail.IMAPMailAdapter;
import org.junit.Test;

public class MailTests extends MasterTest {

    IMAPMailAdapter mailAdapterService = new IMAPMailAdapter(globalProperties.getProperty("host"),
            globalProperties.getProperty("email"),
            passwordsProperties.getProperty("password"),
            globalProperties.getProperty("inboxName"),
            globalProperties.getProperty("processedFolder"),
            globalProperties.getProperty("unprocessableFolder"));

    @Test
    public void testConnection(){
        mailAdapterService.connectToServer();
        mailAdapterService.closeOpenFolders();
    }

    @Test
    public void testSendMail(){
        mailAdapterService.connectToServer();
        mailAdapterService.sendMail(new String[]{"robert.erdin@gmail.com"}, "Whatistics test mail", "Test successful");
        mailAdapterService.closeOpenFolders();
    }
}