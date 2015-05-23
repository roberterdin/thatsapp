package com.whatistics.backend.tasks;

import com.sun.mail.imap.IMAPMessage;
import com.whatistics.backend.configuration.GlobalConfig;
import com.whatistics.backend.services.MailAdapterService;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TimerTask;
import java.util.TreeSet;

/**
 * Created by robert on 23/05/15.
 */
public class MailFetcherTask extends TimerTask {

    private MailAdapterService mailAdapterService = new MailAdapterService(GlobalConfig.HOST, GlobalConfig.EMAIL, GlobalConfig.PASSWORD);

    // threadsafe, sorted set
    SortedSet<IMAPMessage> pendingMessages = Collections.synchronizedSortedSet(new TreeSet<IMAPMessage>());


    @Override
    public void run() {
        mailAdapterService.fetchMails();
        for(Message message: mailAdapterService.getMails()) {
            pendingMessages.add((IMAPMessage) message);
        }
    }
}
