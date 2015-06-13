package com.whatistics.backend.parser;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.whatistics.backend.Service;
import com.whatistics.backend.mail.IMAPMailAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author robert
 */
@Singleton
public class ParserService implements Service {
    final Logger logger = LoggerFactory.getLogger(ParserService.class);

    ExecutorService pendingMessagesExecutorService;
    List<TimeFormat> timeFormats;

    @Inject
    public ParserService(PendingMessagesExecutorServiceProvider pendingMessagesExecutorServiceProvider,
                         TimeFormatsProvider dateFormatsProvider){

        pendingMessagesExecutorService = pendingMessagesExecutorServiceProvider.get();
        timeFormats = dateFormatsProvider.get();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop(){

    }

    public void parseMessage(InputStream inputStream){
        pendingMessagesExecutorService.submit(
                new ParserWorker(inputStream, timeFormats)
        );
    }
}
