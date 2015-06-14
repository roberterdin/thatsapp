package com.whatistics.backend.parser.mock;

import com.google.inject.Inject;
import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.parser.ParserService;
import com.whatistics.backend.parser.ParserWorker;
import com.whatistics.backend.parser.TimeFormat;
import com.whatistics.backend.parser.TimeFormatsProvider;

import java.io.InputStream;
import java.util.List;

/**
 * @author robert
 */
public class MockParserService implements ParserService {

    List<TimeFormat> timeFormats;

    @Inject
    public MockParserService(TimeFormatsProvider timeFormatsProvider){
        timeFormats = timeFormatsProvider.get();
    }

    @Override
    public void parseMessage(InputStream inputStream) {
        // this will trigger parsing but nothing will be stored in the database
        new ParserWorker(inputStream, timeFormats, this);
    }

    @Override
    public void storeConversation(Conversation conversation) {
        // do nothing
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
