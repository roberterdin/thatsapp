package com.whatistics.backend.mail;

import com.google.inject.Inject;
import com.sun.mail.imap.IMAPMessage;
import com.whatistics.backend.shared.PendingMessagesProvider;

import javax.mail.Message;
import java.util.TimerTask;

/**
 * Fetch mail and store it in the queue for the consumer threads. The queue is in fact a sorted set
 * to be able to reconnect to the mail server and use the inbox as a persistent storage of mails to be processed.
 */
public class MailFetcherTask extends TimerTask {

    private MailAdapterService mailAdapterService;

    private PendingMessagesProvider pendingMessagesProvider;

    @Inject
    public MailFetcherTask(MailAdapterService mailAdapterService, PendingMessagesProvider pendingMessagesProvider){
        this.mailAdapterService = mailAdapterService;
        this.pendingMessagesProvider = pendingMessagesProvider;
    }

    @Override
    public void run() {
        mailAdapterService.fetchMails();
        for(Message message: mailAdapterService.getMails()) {
            pendingMessagesProvider.get().add((IMAPMessage) message);
        }
    }
}