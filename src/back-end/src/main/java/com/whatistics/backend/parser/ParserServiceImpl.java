package com.whatistics.backend.parser;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.whatistics.backend.dal.DataStoreProvider;
import com.whatistics.backend.mail.MailUtilities;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author robert
 */
@Singleton
public class ParserServiceImpl extends ParserService {
    private final Logger logger = LoggerFactory.getLogger(ParserServiceImpl.class);

    private final ExecutorService pendingMessagesExecutorService;
    private final List<TimeFormat> timeFormats;
    private final Datastore ds;

    @Inject
    public ParserServiceImpl(PendingMessagesExecutorServiceProvider pendingMessagesExecutorServiceProvider,
                             TimeFormatsProvider dateFormatsProvider,
                             DataStoreProvider dataStoreProvider
                             ){

        pendingMessagesExecutorService = pendingMessagesExecutorServiceProvider.get();
        timeFormats = dateFormatsProvider.get();
        this.ds = dataStoreProvider.get();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop(){
        pendingMessagesExecutorService.shutdown();
    }

    /**
     * TODO: move this logic out of the parser module?
     * @param message
     */
    @Override
    public void parseMessage(Message message){

        CompletableFuture.supplyAsync(() ->
                        new ParserWorker(MailUtilities.getCleanAttachments(message).firstEntry().getValue(), timeFormats, ds).call()
                , pendingMessagesExecutorService
        ).thenApply(conversation -> {

            // add message (transient)
            conversation.setOriginalMessage(message);

            // add sender
            try {
                conversation.setSubmittedBy(((InternetAddress) message.getFrom()[0]).getAddress());
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            // pass to observer
            this.setChanged();
            this.notifyObservers(conversation);
            return conversation;
        });

        try {
            logger.debug("Parser runnable created for message: " + ((InternetAddress)message.getFrom()[0]).getAddress() + " writes " + message.getSubject());
        } catch (MessagingException e) {
            logger.debug("Parser runnable created for message", message);
        }
    }
}