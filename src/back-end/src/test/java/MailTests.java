import com.whatistics.backend.configuration.GlobalConfig;
import com.whatistics.backend.mail.IMAPMailAdapterService;
import org.junit.Test;

public class MailTests {

    @Test
    public void testConnection(){

        // This should be sufficient because the connection is made in the constructor
        IMAPMailAdapterService mailAdapterService = new IMAPMailAdapterService(GlobalConfig.HOST, GlobalConfig.EMAIL, GlobalConfig.PASSWORD);
    }


}
