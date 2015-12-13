package com.whatistics.backend.mail;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.whatistics.backend.configuration.GlobalConfig;
import com.whatistics.backend.parser.ParserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.TimerTask;

/**
 * Fetch mail and store it in the queue for the consumer threads. The queue is in fact a sorted set
 * to be able to reconnect to the mail server and use the inbox as a persistent storage of mails to be processed.
 */
public class MailFetcherTask extends TimerTask {

    private final Logger logger = LoggerFactory.getLogger(MailFetcherTask.class);

    private final MailAdapter mailAdapterService;
    private final ParserServiceImpl parserService;
    private final String inboxName;
    private final String unprocessableFolder;

    @Inject
    public MailFetcherTask(MailAdapter mailAdapterService,
                           ParserServiceImpl parserService,
                           @Named("inboxName") String inboxName,
                           @Named("unprocessableFolder") String unprocessableFolder) {
        this.mailAdapterService = mailAdapterService;
        this.parserService = parserService;

        this.inboxName = inboxName;
        this.unprocessableFolder = unprocessableFolder;
    }

    @Override
    public void run() {
        logger.debug("Running MailFetcherTask");
        mailAdapterService.fetchMails();
        for (Message message : mailAdapterService.getMails()) {
            // This check leads to retrieving the attachments twice.
            if (MailUtilities.isValid(message)) {
                parserService.parseMessage(message );
            } else {
                // TODO: do something with these messages
                logger.info("Unprocessable mail. Move it to according folder", message);
                mailAdapterService.moveToFolder(message, inboxName, unprocessableFolder);
            }
        }
    }
}