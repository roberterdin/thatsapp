package com.whatistics.backend.parser;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.whatistics.backend.dal.DataStoreProvider;
import com.whatistics.backend.mail.MailUtilities;
import com.whatistics.backend.parser.language.LanguageDetector;
import com.whatistics.backend.parser.language.LanguageDetectorWorker;
import com.whatistics.backend.parser.language.ProbabilisticLocale;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author robert
 */
@Singleton
public class ParserServiceImpl extends ParserService {
    private final Logger logger = LoggerFactory.getLogger(ParserServiceImpl.class);

    private final ExecutorService pendingMessagesExecutorService;
    private final List<TimeFormat> timeFormats;
    private final Datastore ds;
    private final LanguageDetector languageDetector;

    @Inject
    public ParserServiceImpl(PendingMessagesExecutorServiceProvider pendingMessagesExecutorServiceProvider,
                             TimeFormatsProvider dateFormatsProvider,
                             DataStoreProvider dataStoreProvider,
                             LanguageDetector languageDetector
    ) {

        this.pendingMessagesExecutorService = pendingMessagesExecutorServiceProvider.get();
        this.timeFormats = dateFormatsProvider.get();
        this.ds = dataStoreProvider.get();
        this.languageDetector = languageDetector;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        pendingMessagesExecutorService.shutdown();
    }

    /**
     * TODO: move this logic out of the parser module?
     *
     * @param message
     */
    @Override
    public void parseMessage(Message message) {

        CompletableFuture.supplyAsync(() ->
                        new ParserWorker(MailUtilities.getCleanAttachments(message).firstEntry().getValue(), timeFormats, ds).call()
                , pendingMessagesExecutorService)
                .thenApply(conversation -> {

                    // add message (transient)
                    conversation.setOriginalMessage(message);

                    // add sender
                    try {
                        conversation.setSubmittedBy(((InternetAddress) message.getFrom()[0]).getAddress());
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }


                    return conversation;
                })
                .thenApplyAsync(conversation -> new LanguageDetectorWorker(this.languageDetector, conversation).call()).thenApply(conversation -> {
            // pass to observer
            this.setChanged();
            this.notifyObservers(conversation);

            return conversation;
        });

        try {
            logger.debug("Parser runnable created for message: " + ((InternetAddress) message.getFrom()[0]).getAddress() + " writes " + message.getSubject());
        } catch (MessagingException e) {
            logger.debug("Parser runnable created for message", message);
        }
    }
}