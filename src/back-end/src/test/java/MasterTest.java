import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Load configuration for use in tests.
 * @author robert
 */
public class MasterTest {
    protected Properties globalProperties = new Properties();
    protected Properties passwordsProperties= new Properties();

    MasterTest(){
        try {
            System.out.println("Working Directory = " +
                    System.getProperty("user.dir"));
            this.globalProperties.load(new FileReader("build/resources/main/global.properties"));
            this.passwordsProperties.load(new FileReader("build/resources/main/password.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
