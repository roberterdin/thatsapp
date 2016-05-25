import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.GlobalStatistics;
import com.whatistics.backend.parser.ParserWorker;
import com.whatistics.backend.parser.TimeFormatsProvider;
import com.whatistics.backend.parser.language.LanguageDetector;
import com.whatistics.backend.parser.language.LanguageDetectorOptimaize;
import com.whatistics.backend.parser.language.LanguageDetectorWorker;
import com.whatistics.backend.statistics.CachingStopWordsProvider;
import com.whatistics.backend.statistics.MediaPatternProvider;
import com.whatistics.backend.statistics.StatisticsWorker;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author robert
 */
public class StatisticsTests extends MasterTest {

    private static TimeFormatsProvider timeFormatsProvider = new TimeFormatsProvider();

    @Test
    public void testEmoticons() throws FileNotFoundException {

        InputStream is = new FileInputStream("../../resources/chatHistories/testing/statistics.txt");
        ParserWorker parserWorker = new ParserWorker(is, timeFormatsProvider.get(), ds);

        Conversation conversation = parserWorker.call();

        StatisticsWorker statisticsWorker = new StatisticsWorker(new MediaPatternProvider(),
                new CachingStopWordsProvider(),
                globalProperties.getIntProp("statisticsLength"));

        GlobalStatistics result = statisticsWorker.compute(conversation);

        assertEquals(Integer.valueOf(5), result.getStatistics().getEmoticons().get("\uD83D\uDE02"));

    }

    @Test
    public void testVocabulary() throws FileNotFoundException {

        InputStream is = new FileInputStream("../../resources/chatHistories/testing/statistics.txt");
        ParserWorker parserWorker = new ParserWorker(is, timeFormatsProvider.get(), ds);

        Conversation conversation = parserWorker.call();

        StatisticsWorker statisticsWorker = new StatisticsWorker(new MediaPatternProvider(),
                new CachingStopWordsProvider(),
                globalProperties.getIntProp("statisticsLength"));

        GlobalStatistics result = statisticsWorker.compute(conversation);

        assertEquals(Integer.valueOf(4), result.getStatistics().getVocabulary().get("cock"));

    }

    @Test
    public void testMedia() throws FileNotFoundException {

        InputStream is = new FileInputStream("../../resources/chatHistories/testing/statistics.txt");
        ParserWorker parserWorker = new ParserWorker(is, timeFormatsProvider.get(), ds);

        Conversation conversation = parserWorker.call();

        StatisticsWorker statisticsWorker = new StatisticsWorker(new MediaPatternProvider(),
                new CachingStopWordsProvider(),
                globalProperties.getIntProp("statisticsLength"));

        GlobalStatistics result = statisticsWorker.compute(conversation);

        assertEquals(3, result.getStatistics().getMediaAmount());

    }


    @Test
    public void testStopWords() throws FileNotFoundException {
        InputStream is = new FileInputStream("../../resources/chatHistories/testing/english.txt");
        ParserWorker parserWorker = new ParserWorker(is, timeFormatsProvider.get(), ds);

        Conversation conversation = parserWorker.call();

        LanguageDetector languageDetector = new LanguageDetectorOptimaize();

        new LanguageDetectorWorker(languageDetector, conversation).call();

        StatisticsWorker statisticsWorker = new StatisticsWorker(new MediaPatternProvider(),
                new CachingStopWordsProvider(),
                globalProperties.getIntProp("statisticsLength"));

        GlobalStatistics result = statisticsWorker.compute(conversation);

        assertEquals(false, result.getStatistics().getVocabulary().containsKey("i"));
    }
}
