package com.whatistics.backend;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.whatistics.backend.configuration.GlobalConfig;
import com.whatistics.backend.dal.DataStoreProvider;
import com.whatistics.backend.mail.MailService;
import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.parser.ParserService;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author robert
 */
@Singleton
public class WhatisticsService implements ObservingService<Conversation> {

    final Logger logger = LoggerFactory.getLogger(WhatisticsService.class);

    MailService mailService;
    ParserService parserService;
    Datastore ds;

    @Inject
    public WhatisticsService(MailService mailService,
                             ParserService parserService,
                             DataStoreProvider dataStoreProvider){

        this.mailService = mailService;
        this.parserService = parserService;
        this.ds = dataStoreProvider.get();
    }


    @Override
    public void update(ObservableService<Conversation> service, Conversation data) {
        if(data.getMessages().size() > 1){
            ds.save(data);
            if (data.getId() != null){
                mailService.moveToFolder(data.getOriginalMessage(), GlobalConfig.INBOX_NAME, GlobalConfig.PROCESSED_FOLDER);
                mailService.sendMail(data.getSubmittedBy(), "Here are yor statistics", data.getId().toHexString());
            }
        }else {
            logger.error("Parsing failed for message", data.getOriginalMessage());
            mailService.moveToFolder(data.getOriginalMessage(), GlobalConfig.INBOX_NAME, GlobalConfig.UNPROCESSABLE_FOLDER);
        }
    }

    @Override
    public void start() {
        this.mailService.start();
        this.parserService.addObserver(this);
        this.parserService.start();
    }

    @Override
    public void stop() {
        this.mailService.stop();
        this.parserService.stop();
    }
}