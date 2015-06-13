package com.whatistics.backend.mail;

import com.google.inject.Inject;
import com.whatistics.backend.configuration.GlobalConfig;
import com.whatistics.backend.parser.ParserService;
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

    final Logger logger = LoggerFactory.getLogger(MailFetcherTask.class);

    private MailAdapter mailAdapterService;
    private ParserService parserService;

    @Inject
    public MailFetcherTask(MailAdapter mailAdapterService, ParserService parserService) {
        this.mailAdapterService = mailAdapterService;
        this.parserService = parserService;
    }

    @Override
    public void run() {
        mailAdapterService.fetchMails();
        for (Message message : mailAdapterService.getMails()) {

            // This check leads to retrieving the attachments twice.
            if (MailUtilities.isValid(message)) {
                parserService.parseMessage(
                        MailUtilities.getAttachments(message).get(0)
                );

                try {
                    logger.debug("Parser runnable created for message: " + ((InternetAddress)message.getFrom()[0]).getAddress() + " writes " + message.getSubject());
                } catch (MessagingException e) {
                    logger.debug("Parser runnable created for message", message);
                }

            } else {
                // TODO: do something with these messages
                mailAdapterService.moveToFolder(message, GlobalConfig.INBOX_NAME, GlobalConfig.UNPROCESSABLE_FOLDER);
            }
        }
    }
}