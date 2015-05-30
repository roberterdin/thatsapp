package com.whatistics.backend.mail;

import com.google.inject.Inject;
import com.whatistics.backend.Service;

/**
 * @author robert
 */
public class MailService implements Service {

    private MailAdapterService mailAdapterService;
    private MailFetcherTask mailFetcherTask;

    @Inject
    public MailService(MailAdapterService mailAdapterService, MailFetcherTask mailFetcherTask){
        this.mailAdapterService = mailAdapterService;
        this.mailFetcherTask = mailFetcherTask;
    }

    @Override
    public void start() {
        this.mailFetcherTask.run();
    }

    @Override
    public void stop() {
        mailAdapterService.closeInbox();
    }
}
