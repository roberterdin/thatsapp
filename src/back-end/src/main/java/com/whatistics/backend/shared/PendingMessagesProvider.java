package com.whatistics.backend.shared;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sun.mail.imap.IMAPMessage;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;


public class PendingMessagesProvider implements Provider<SortedSet<IMAPMessage>> {

    private SortedSet<IMAPMessage> pendingMessages;

    @Inject
    public PendingMessagesProvider(){
    }

    @Override
    public SortedSet<IMAPMessage> get() {
        if (pendingMessages == null)
            this.pendingMessages = Collections.synchronizedSortedSet(new TreeSet<IMAPMessage>(new IMAPMessageComparator()));

        return this.pendingMessages;
    }
}
