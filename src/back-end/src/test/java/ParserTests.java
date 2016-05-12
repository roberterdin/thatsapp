import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.parser.ParserWorker;
import com.whatistics.backend.parser.TimeFormatsProvider;
import com.whatistics.backend.parser.mock.MockParserService;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author robert
 */
public class ParserTests {


    @Test
    public void testParsing() throws FileNotFoundException {

        TimeFormatsProvider timeFormatsProvider = new TimeFormatsProvider();
        InputStream is = new FileInputStream("../../resources/chatHistories/android-en_us-24h_2.txt");
        ParserWorker parserWorker = new ParserWorker(is, timeFormatsProvider.get());

        parserWorker.call();
    }
}
