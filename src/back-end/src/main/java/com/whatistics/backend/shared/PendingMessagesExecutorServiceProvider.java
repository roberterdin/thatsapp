package com.whatistics.backend.shared;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.whatistics.backend.configuration.GlobalConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author robert
 */
@Singleton
public class PendingMessagesExecutorServiceProvider implements Provider<ExecutorService> {

    private ExecutorService pendingMessagesExecutorService;

    @Inject
    public PendingMessagesExecutorServiceProvider(){
    }

    @Override
    public ExecutorService get() {
        if (this.pendingMessagesExecutorService == null)
            this.pendingMessagesExecutorService = Executors.newFixedThreadPool(GlobalConfig.NO_OF_PARSERS);

        return this.pendingMessagesExecutorService;
    }
}
