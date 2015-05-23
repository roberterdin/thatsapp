import com.whatistics.backend.configuration.GlobalConfig;
import com.whatistics.backend.services.MailAdapterService;
import org.junit.Test;

public class MailTests {

    @Test
    public void testConnection(){

        // This should be sufficient because the connection is made in the constructor
        MailAdapterService mailAdapterService = new MailAdapterService(GlobalConfig.HOST, GlobalConfig.EMAIL, GlobalConfig.PASSWORD);
    }


}
