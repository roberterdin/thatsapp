package com.whatistics.backend.mail;

import com.google.inject.Inject;
import com.sun.mail.imap.IMAPMessage;
import com.whatistics.backend.mail.utils.IMAPMessageComparator;

import javax.mail.Message;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TimerTask;
import java.util.TreeSet;

/**
 * Fetch mail and store it in the queue for the consumer threads. The queue is in fact a sorted set
 * to be able to reconnect to the mail server and use the inbox as a persistent storage of mails to be processed.
 */
public class MailFetcherTask extends TimerTask {

    private MailAdapterService mailAdapterService;

    // threadsafe, sorted set
    SortedSet<IMAPMessage> pendingMessages = Collections.synchronizedSortedSet(new TreeSet<IMAPMessage>(new IMAPMessageComparator()));
    @Inject
    public MailFetcherTask(MailAdapterService mailAdapterService){
        this.mailAdapterService = mailAdapterService;
    }

    @Override
    public void run() {
        mailAdapterService.fetchMails();
        for(Message message: mailAdapterService.getMails()) {
            pendingMessages.add((IMAPMessage) message);
        }
    }
}
