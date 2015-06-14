package com.whatistics.backend.parser;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.whatistics.backend.Service;
import com.whatistics.backend.dal.DataStoreProvider;
import com.whatistics.backend.model.Conversation;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author robert
 */
@Singleton
public class ParserServiceImpl implements ParserService {
    final Logger logger = LoggerFactory.getLogger(ParserServiceImpl.class);

    ExecutorService pendingMessagesExecutorService;
    List<TimeFormat> timeFormats;
    Datastore ds;

    @Inject
    public ParserServiceImpl(PendingMessagesExecutorServiceProvider pendingMessagesExecutorServiceProvider,
                             TimeFormatsProvider dateFormatsProvider,
                             DataStoreProvider dataStoreProvider){

        pendingMessagesExecutorService = pendingMessagesExecutorServiceProvider.get();
        timeFormats = dateFormatsProvider.get();
        ds = dataStoreProvider.get();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop(){
        pendingMessagesExecutorService.shutdown();
    }

    @Override
    public void parseMessage(InputStream inputStream){
        pendingMessagesExecutorService.submit(
                new ParserWorker(inputStream, timeFormats, this)
        );
    }

    @Override
    public void storeConversation(Conversation conversation){

    }
}
