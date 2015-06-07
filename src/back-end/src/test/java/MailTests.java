import com.whatistics.backend.configuration.GlobalConfig;
import com.whatistics.backend.mail.IMAPMailAdapter;
import org.junit.Test;

public class MailTests {

    @Test
    public void testConnection(){

        IMAPMailAdapter mailAdapterService = new IMAPMailAdapter(GlobalConfig.HOST, GlobalConfig.EMAIL, GlobalConfig.PASSWORD);
        mailAdapterService.connectToServer();
    }
}