package com.whatistics.backend.parser;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.whatistics.backend.Service;

import javax.mail.Message;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author robert
 */
@Singleton
public class ParserService implements Service {

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

    public void parseMessage(Message eMailMessage){
        pendingMessagesExecutorService.submit(
                new ParserWorker(eMailMessage, timeFormats)
        );
    }
}
