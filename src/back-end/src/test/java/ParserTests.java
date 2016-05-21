import com.whatistics.backend.parser.ParserWorker;
import com.whatistics.backend.parser.TimeFormatsProvider;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author robert
 */
public class ParserTests extends MasterTest{


    @Test
    public void testParsing() throws FileNotFoundException {

        TimeFormatsProvider timeFormatsProvider = new TimeFormatsProvider();
        InputStream is = new FileInputStream("../../resources/chatHistories/dave.txt");
        ParserWorker parserWorker = new ParserWorker(is, timeFormatsProvider.get());

        parserWorker.call();
    }
}
