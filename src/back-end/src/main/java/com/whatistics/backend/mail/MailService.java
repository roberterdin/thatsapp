package com.whatistics.backend.mail;

import com.google.inject.Inject;
import com.whatistics.backend.Service;

/**
 * @author robert
 */
public class MailService implements Service {

    private MailAdapter mailAdapterService;
    private MailFetcherTask mailFetcherTask;

    @Inject
    public MailService(MailAdapter mailAdapterService, MailFetcherTask mailFetcherTask){
        this.mailAdapterService = mailAdapterService;
        this.mailFetcherTask = mailFetcherTask;
    }

    @Override
    public void start() {
        this.mailAdapterService.connectToServer();
        this.mailFetcherTask.run();
    }

    @Override
    public void stop() {
        mailAdapterService.closeOpenFolders();
    }
}
