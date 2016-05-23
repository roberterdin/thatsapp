import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.parser.ParserWorker;
import com.whatistics.backend.parser.TimeFormatsProvider;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author robert
 */
public class ParserTests extends MasterTest{

    private static TimeFormatsProvider timeFormatsProvider = new TimeFormatsProvider();



    @Test
    public void testMessageAndParticipantCount() throws FileNotFoundException {

        InputStream is = new FileInputStream("../../resources/chatHistories/testing/multiline.txt");
        ParserWorker parserWorker = new ParserWorker(is, timeFormatsProvider.get(), ds);

        Conversation result = parserWorker.call();

        assertEquals(11, result.getMessages().size());
        assertEquals(4, result.getParticipants().size());
    }


    @Test
    public void testChangingTimeFormats() throws FileNotFoundException {

        InputStream is = new FileInputStream("../../resources/chatHistories/testing/changeDateFormat.txt");
        ParserWorker parserWorker = new ParserWorker(is, timeFormatsProvider.get(), ds);

        Conversation result = parserWorker.call();

        assertEquals(33, result.getMessages().size());
    }
}
