package com.whatistics.backend.shared;

import com.sun.mail.imap.IMAPMessage;
import com.whatistics.backend.WhatisticsBackend;
import com.whatistics.backend.mail.MailAdapter;

import java.util.Comparator;

/**
 * Sort IMAP Messages in ascending order.
 * @author robert
 */
public class IMAPMessageComparator implements Comparator<IMAPMessage> {

    MailAdapter mailAdapterService = WhatisticsBackend.getInjector()
            .getInstance(MailAdapter.class);

    @Override
    public int compare(IMAPMessage o1, IMAPMessage o2) {
        return mailAdapterService.getUID(o1) < mailAdapterService.getUID(o2) ? -1 : mailAdapterService.getUID(o1) == mailAdapterService.getUID(o2) ? 0 : 1;
    }
}
