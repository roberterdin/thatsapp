package com.whatistics.backend.parser;

import com.google.inject.Inject;
import com.whatistics.backend.Service;
import com.whatistics.backend.configuration.GlobalConfig;
import com.whatistics.backend.shared.PendingMessagesExecutorServiceProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author robert
 */
public class ParserService implements Service {

    ExecutorService pendingMessagesExecutorService;

    @Inject
    public ParserService(PendingMessagesExecutorServiceProvider pendingMessagesExecutorServiceProvider){
        pendingMessagesExecutorService = pendingMessagesExecutorServiceProvider.get();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        pendingMessagesExecutorService.shutdown();
    }
}
