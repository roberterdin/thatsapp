package com.whatistics.backend.mail;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.whatistics.backend.Service;

import javax.mail.Message;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author robert
 */
public class MailService implements Service {

    private final MailAdapter mailAdapterService;
    private final MailFetcherTask mailFetcherTask;
    private final ScheduledExecutorService scheduledExecutorService;

    private final int maxMailFetcherAmount;
    private final int mailFetchingInterval;

    @Inject
    public MailService(MailAdapter mailAdapterService,
                       MailFetcherTask mailFetcherTask,
                       @Named("maxMailFetcherAmount") int maxMailFetcherAmount,
                       @Named("mailFetchingInterval") int mailFetchingInterval){

        this.mailAdapterService = mailAdapterService;
        this.mailFetcherTask = mailFetcherTask;

        this.maxMailFetcherAmount = maxMailFetcherAmount;
        this.mailFetchingInterval = mailFetchingInterval;

        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(this.maxMailFetcherAmount);
    }

    @Override
    public void start() {
        this.mailAdapterService.connectToServer();

        this.scheduledExecutorService.scheduleAtFixedRate(this.mailFetcherTask, 0, this.mailFetchingInterval, TimeUnit.SECONDS);
        // todo: execute with timer
//        this.mailFetcherTask.run();
    }

    @Override
    public void stop() {
        mailAdapterService.closeOpenFolders();
        this.scheduledExecutorService.shutdown();
    }

    public void moveToFolder(Message message, String sourceFolder, String destFolder){
        this.mailAdapterService.moveToFolder(message, sourceFolder, destFolder);
    }

    public void sendMail(String to, String subject, String body){
        this.mailAdapterService.sendMail(new String[]{to}, subject, body);
    }
}