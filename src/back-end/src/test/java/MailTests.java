import com.whatistics.backend.mail.IMAPMailAdapter;
import org.junit.Test;

public class MailTests extends MasterTest {

    IMAPMailAdapter mailAdapterService = new IMAPMailAdapter(globalProperties.getProperty("email"),
            globalProperties.getProperty("smtpHostName"),
            globalProperties.getProperty("imapHostName"),
            globalProperties.getProperty("dev.mail.password"),
            globalProperties.getProperty("inboxName"),
            globalProperties.getProperty("processedFolder"),
            globalProperties.getProperty("unprocessableFolder"),
            globalProperties.getProperty("mailDomain"));

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