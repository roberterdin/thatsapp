import com.mongodb.MongoClient;
import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.GlobalStatistics;
import com.whatistics.backend.model.Message;
import com.whatistics.backend.rest.RestService;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by robert on 18/05/15.
 */
public class EnvironmentTests extends MasterTest{

    @Test
    public void testTest() {
        assertEquals("onetwo", "onetwo");
    }

    @Test
    public void testMongoConnection(){
        Datastore ds = new Morphia().createDatastore(
                new MongoClient(globalProperties.getProperty("mongoClientHostname")),
                globalProperties.getProperty("dbName"));
        assertNotNull(ds);



    }

    @Test
    public void testCRUD(){
        // CREATE
        Datastore ds = new Morphia().createDatastore(new MongoClient(globalProperties.getProperty("mongoClientHostname")), globalProperties.getProperty("dbTestName"));
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

    @Test
    public void testRest(){

        RestService restService = new RestService();
        restService.start();

        try {
            TimeUnit.SECONDS.sleep(5);
            URL url = new URL("http://localhost:8080");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            assertEquals(connection.getResponseCode(), 200);
            assertEquals(connection.getHeaderField("Content-Type"), "application/hal+json");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to create HTTP connection to test RESTHeart");
        } finally {
            restService.stop();
        }
    }
}
