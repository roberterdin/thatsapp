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
        InputStream is = new FileInputStream("../../resources/mashup.txt");
        ParserWorker parserWorker = new ParserWorker(is, timeFormatsProvider.get(), new MockParserService(timeFormatsProvider));

        parserWorker.call();

    }
}
