package com.whatistics.backend.parser.mock;

import com.google.inject.Inject;
import com.whatistics.backend.mail.MailUtilities;
import com.whatistics.backend.parser.ParserService;
import com.whatistics.backend.parser.ParserWorker;
import com.whatistics.backend.parser.TimeFormat;
import com.whatistics.backend.parser.TimeFormatsProvider;

import javax.mail.Message;
import java.util.List;

/**
 * @author robert
 */
public class MockParserService extends ParserService {

    List<TimeFormat> timeFormats;

    @Inject
    public MockParserService(TimeFormatsProvider timeFormatsProvider){
        timeFormats = timeFormatsProvider.get();
    }

    @Override
    public void parseMessage(Message message) {
        // this will trigger parsing but nothing will be stored in the database
        new ParserWorker(MailUtilities.getAttachments(message).get(0), timeFormats).call();
    }

    @Override
    public void start() {
        // do nothing
    }

    @Override
    public void stop() {
        // do nothing
    }

}