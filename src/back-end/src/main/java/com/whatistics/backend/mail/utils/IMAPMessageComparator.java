package com.whatistics.backend.mail.utils;

import com.google.inject.Guice;
import com.sun.mail.imap.IMAPMessage;
import com.whatistics.backend.WhatisticsBackend;
import com.whatistics.backend.mail.MailAdapterService;
import com.whatistics.backend.mail.MailModule;

import java.util.Comparator;

/**
 * @author Robert
 */
public class IMAPMessageComparator implements Comparator<IMAPMessage> {

    MailAdapterService mailAdapterService = WhatisticsBackend.getInjector()
            .getInstance(MailAdapterService.class);

    @Override
    public int compare(IMAPMessage o1, IMAPMessage o2) {
        return mailAdapterService.getUID(o1) < mailAdapterService.getUID(o2) ? -1 : mailAdapterService.getUID(o1) == mailAdapterService.getUID(o2) ? 0 : 1;
    }
}
