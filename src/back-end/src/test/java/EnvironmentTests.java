import com.mongodb.MongoClient;
import com.whatistics.backend.configuration.LocalConfig;
import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.Message;
import org.bson.types.ObjectId;
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
        Datastore ds = new Morphia().createDatastore(new MongoClient(LocalConfig.MONGO_CLIENT_HOSTNAME), LocalConfig.DB_NAME);
        assertNotNull(ds);
    }

    @Test
    public void testCRUD(){
        // CREATE
        Datastore ds = new Morphia().createDatastore(new MongoClient(LocalConfig.MONGO_CLIENT_HOSTNAME), LocalConfig.DB_NAME);
        Conversation conversation = new Conversation();
        conversation.getMessages().add(new Message().fillWithRandom());
        conversation.getMessages().add(new Message().fillWithRandom());
        conversation.getMessages().add(new Message().fillWithRandom());

        ds.save(conversation);
        assertNotNull(conversation.getId());

        // UPDATE and READ
        ObjectId convId = conversation.getId();
        conversation.getMessages().add(new Message().fillWithRandom());
        ds.save(conversation);

        Conversation retrievedConv = ds.find(Conversation.class, "id", convId).get();

        assertEquals(retrievedConv.getMessages().size(), 4);

        // DELETE
        ds.findAndDelete(ds.createQuery(Conversation.class).filter("id", convId));
        assertEquals(ds.find(Conversation.class, "id", convId).asList().size(), 0);
    }

}
