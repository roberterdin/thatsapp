import com.mongodb.MongoClient;
import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.GlobalStatistics;
import com.whatistics.backend.parser.ParserWorker;
import com.whatistics.backend.parser.TimeFormatsProvider;
import com.whatistics.backend.statistics.EmojiPatternProvider;
import com.whatistics.backend.statistics.MediaPatternProvider;
import com.whatistics.backend.statistics.StatisticsWorker;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by robert on 21/05/16.
 */
public class ParseStatisticsStoreIntegration extends MasterTest {

    @Test
    public void testParseStatisticsStoreIntegration() throws FileNotFoundException {

        Datastore ds = new Morphia().createDatastore(new MongoClient(globalProperties.getProperty("mongoClientHostname")), globalProperties.getProperty("dbTestName"));

        TimeFormatsProvider timeFormatsProvider = new TimeFormatsProvider();
        InputStream is = new FileInputStream("../../resources/chatHistories/seebi_2.txt");
        ParserWorker parserWorker = new ParserWorker(is, timeFormatsProvider.get());

        Conversation conversation = parserWorker.call();

        StatisticsWorker statisticsWorker = new StatisticsWorker(new MediaPatternProvider(), new EmojiPatternProvider(), globalProperties.getIntProp("statisticsLength"));

        GlobalStatistics gs = statisticsWorker.compute(conversation);

        gs.saveObjectGraph(ds);
    }
}
