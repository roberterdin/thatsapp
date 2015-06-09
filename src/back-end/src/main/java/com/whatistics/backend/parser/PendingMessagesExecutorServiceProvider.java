package com.whatistics.backend.parser;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.whatistics.backend.configuration.GlobalConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author robert
 */
@Singleton
public class PendingMessagesExecutorServiceProvider implements Provider<ExecutorService> {

    private ExecutorService pendingMessagesExecutorService;
    private int noOfParsers;

    @Inject
    public PendingMessagesExecutorServiceProvider(@Named("parsers") int parsers){
        noOfParsers = parsers;
    }

    @Override
    public ExecutorService get() {
        if (this.pendingMessagesExecutorService == null)
            this.pendingMessagesExecutorService = Executors.newFixedThreadPool(noOfParsers);

        return this.pendingMessagesExecutorService;
    }
}
