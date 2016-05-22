import com.mongodb.MongoClient;
import com.whatistics.backend.configuration.TypedProperties;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.FileReader;
import java.io.IOException;

/**
 * Load configuration for use in tests.
 * @author robert
 */
public class MasterTest {
    protected TypedProperties globalProperties = new TypedProperties();

    protected Datastore ds;

    MasterTest(){
        try {
            this.globalProperties.load(this.getClass().getClassLoader().getResourceAsStream("global.properties"));
            this.globalProperties.load(this.getClass().getClassLoader().getResourceAsStream("password.properties"));
            this.globalProperties.load(this.getClass().getClassLoader().getResourceAsStream("dev.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.ds = new Morphia().createDatastore(new MongoClient(globalProperties.getProperty("mongoClientHostname")), globalProperties.getProperty("dbTestName"));
    }
}
