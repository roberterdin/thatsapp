package com.whatistics.backend.mail;

import com.google.inject.Inject;
import com.whatistics.backend.Service;

import javax.mail.Message;

/**
 * @author robert
 */
public class MailService implements Service {

    private final MailAdapter mailAdapterService;
    private final MailFetcherTask mailFetcherTask;

    @Inject
    public MailService(MailAdapter mailAdapterService, MailFetcherTask mailFetcherTask){
        this.mailAdapterService = mailAdapterService;
        this.mailFetcherTask = mailFetcherTask;
    }

    @Override
    public void start() {
        this.mailAdapterService.connectToServer();
        // todo: execute with timer
        this.mailFetcherTask.run();
    }

    @Override
    public void stop() {
        mailAdapterService.closeOpenFolders();
    }

    public void moveToFolder(Message message, String sourceFolder, String destFolder){
        this.mailAdapterService.moveToFolder(message, sourceFolder, destFolder);
    }

    public void sendMail(String to, String subject, String body){
        this.mailAdapterService.sendMail(new String[]{to}, subject, body);
    }
}