import com.mongodb.MongoClient;
import com.whatistics.backend.configuration.LocalConfig;
import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.GlobalStatistics;
import com.whatistics.backend.model.Message;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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

    @Test
    public void testRest(){
        // start the restheart server as a process
        // todo: make RESTHeart use configuration file from resources
        // todo: redirect stdout
        Process restProc;
        try {
            restProc = Runtime.getRuntime().exec("java -server -jar build/libs/lib/restheart-1.1.7.jar");


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
                restProc.destroyForcibly();
            }
        } catch (IOException e) {
            fail("Process with RESTHeart server can't be spawned");
            e.printStackTrace();
        }



    }

}
