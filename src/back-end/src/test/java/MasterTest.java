import com.whatistics.backend.configuration.TypedProperties;

import java.io.FileReader;
import java.io.IOException;

/**
 * Load configuration for use in tests.
 * @author robert
 */
public class MasterTest {
    protected TypedProperties globalProperties = new TypedProperties();

    MasterTest(){
        try {
            this.globalProperties.load(this.getClass().getClassLoader().getResourceAsStream("global.properties"));
            this.globalProperties.load(this.getClass().getClassLoader().getResourceAsStream("password.properties"));
            this.globalProperties.load(this.getClass().getClassLoader().getResourceAsStream("dev.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
