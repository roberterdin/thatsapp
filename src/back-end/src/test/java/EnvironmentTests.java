import com.mongodb.MongoClient;
import com.whatistics.backend.configuration.LocalConfig;
import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.GlobalStatistics;
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
        Datastore ds = new Morphia().createDatastore(new MongoClient(LocalConfig.MONGO_CLIENT_HOSTNAME), LocalConfig.DB_TEST_NAME);
        Conversation conversation = new Conversation();
        conversation.addMessage(new Message().fillWithRandom());
        conversation.addMessage(new Message().fillWithRandom());
        conversation.addMessage(new Message().fillWithRandom());

        GlobalStatistics globalStatistics = new GlobalStatistics(conversation);
        globalStatistics.getStatistics().fillWithRandom();

        for (Message message : conversation.getMessages()){
            message.getSender().getStatistics().fillWithRandom();
        }

        globalStatistics.saveObjectGraph(ds);
        assertNotNull(conversation.getId());

        // UPDATE and READ
        ObjectId convId = conversation.getId();
        conversation.addMessage(new Message().fillWithRandom());

        conversation.getParticipants().stream()
                .forEach(e -> {
                            ds.save(e.getStatistics());
                            ds.save(e);
                        }
                );

        ds.save(conversation);

        Conversation retrievedConv = ds.find(Conversation.class, "id", convId).get();

        assertEquals(retrievedConv.getMessages().size(), 4);

        // DELETE
        ds.findAndDelete(ds.createQuery(Conversation.class).filter("id", convId));
        assertEquals(ds.find(Conversation.class, "id", convId).asList().size(), 0);
    }

}
