package com.whatistics.backend.mail;

import com.google.inject.Inject;
import com.whatistics.backend.configuration.GlobalConfig;
import com.whatistics.backend.parser.ParserService;

import javax.mail.Message;
import java.util.TimerTask;

/**
 * Fetch mail and store it in the queue for the consumer threads. The queue is in fact a sorted set
 * to be able to reconnect to the mail server and use the inbox as a persistent storage of mails to be processed.
 */
public class MailFetcherTask extends TimerTask {

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
                parserService.parseMessage(message);
            } else {
                // TODO: do something with these messages
                mailAdapterService.moveToFolder(message, GlobalConfig.INBOX_NAME, GlobalConfig.UNPROCESSABLE_FOLDER);
            }
        }
    }
}