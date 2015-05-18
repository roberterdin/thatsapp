import com.whatistics.backend.configuration.LocalConfig;
import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.Message;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by robert on 18/05/15.
 */
public class EnvironmentTests {

    @Test
    public void testTest() {
        assertEquals("onetwo", "onetwo");
    }

    @Test
    public void testMongoConnection(){
        Datastore ds = new Morphia().createDatastore(LocalConfig.MONGO_CLIENT, LocalConfig.DB_NAME);
        assertNotNull(ds);
    }

    @Test
    public void testCRUD(){
        Datastore ds = new Morphia().createDatastore(LocalConfig.MONGO_CLIENT, LocalConfig.DB_NAME);
        Conversation conversation = new Conversation();
        conversation.getMessages().add(new Message().fillWithRandom());
        conversation.getMessages().add(new Message().fillWithRandom());
        conversation.getMessages().add(new Message().fillWithRandom());

        ds.save(conversation);

        assertNotNull(conversation.getId());
    }

}
