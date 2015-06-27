import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.GlobalStatistics;
import com.whatistics.backend.parser.ParserWorker;
import com.whatistics.backend.parser.TimeFormatsProvider;
import com.whatistics.backend.statistics.EmojiPatternProvider;
import com.whatistics.backend.statistics.MediaPatternProvider;
import com.whatistics.backend.statistics.StatisticsWorker;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author robert
 */
public class StatisticsTests {

    @Test
    public void testStatistics() throws FileNotFoundException {
        TimeFormatsProvider timeFormatsProvider = new TimeFormatsProvider();
        InputStream is = new FileInputStream("../../resources/mashup.txt");
        ParserWorker parserWorker = new ParserWorker(is, timeFormatsProvider.get());

        Conversation conversation = parserWorker.call();

        StatisticsWorker statisticsWorker = new StatisticsWorker(new MediaPatternProvider(), new EmojiPatternProvider());

        statisticsWorker.compute(conversation);

    }
}
