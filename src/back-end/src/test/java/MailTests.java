import com.whatistics.backend.configuration.GlobalConfig;
import com.whatistics.backend.mail.IMAPMailAdapter;
import org.junit.Test;

public class MailTests {

    IMAPMailAdapter mailAdapterService = new IMAPMailAdapter(GlobalConfig.HOST, GlobalConfig.EMAIL, GlobalConfig.PASSWORD, GlobalConfig.INBOX_NAME, GlobalConfig.PROCESSED_FOLDER, GlobalConfig.UNPROCESSABLE_FOLDER);

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