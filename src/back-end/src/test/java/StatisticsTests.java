import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.parser.ParserWorker;
import com.whatistics.backend.parser.TimeFormatsProvider;
import com.whatistics.backend.statistics.MediaPatternProvider;
import com.whatistics.backend.statistics.StatisticsWorker;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author robert
 */
public class StatisticsTests extends MasterTest {

    @Test
    public void testStatistics() throws FileNotFoundException {
        TimeFormatsProvider timeFormatsProvider = new TimeFormatsProvider();
        InputStream is = new FileInputStream("../../resources/chatHistories/k_chat_long.txt");
        ParserWorker parserWorker = new ParserWorker(is, timeFormatsProvider.get(), ds);

        Conversation conversation = parserWorker.call();

        StatisticsWorker statisticsWorker = new StatisticsWorker(new MediaPatternProvider(), globalProperties.getIntProp("statisticsLength"));

        statisticsWorker.compute(conversation);
    }
}
