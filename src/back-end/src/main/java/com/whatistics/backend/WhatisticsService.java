package com.whatistics.backend;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.whatistics.backend.dal.DataStoreProvider;
import com.whatistics.backend.mail.MailService;
import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.GlobalStatistics;
import com.whatistics.backend.parser.ParserService;
import com.whatistics.backend.statistics.StatisticsService;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;

/**
 * @author robert
 */
@Singleton
public class WhatisticsService implements Observer, Service {

    private final Logger logger = LoggerFactory.getLogger(WhatisticsService.class);

    private final MailService mailService;
    private final ParserService parserService;
    private final StatisticsService statisticsService;
    private final Datastore ds;

    private final String inboxName;
    private final String processedFolder;
    private final String unprocessableFolder;

    @Inject
    public WhatisticsService(MailService mailService,
                             ParserService parserService,
                             StatisticsService statisticsService,
                             DataStoreProvider dataStoreProvider,
                             @Named("inboxName") String inboxName,
                             @Named("processedFolder") String processedFolder,
                             @Named("unprocessableFolder") String unprocessableFolder){

        this.mailService = mailService;
        this.parserService = parserService;
        this.statisticsService = statisticsService;
        this.ds = dataStoreProvider.get();

        this.inboxName = inboxName;
        this.processedFolder = processedFolder;
        this.unprocessableFolder = unprocessableFolder;
    }


    @Override
    public void update(Observable service, Object data) {
        if (data instanceof Conversation){
            Conversation conversation = (Conversation)data;
            if(conversation.getMessages().size() > 1){
//                ds.save(conversation);

                // reuses parsing thread to generate statistics

                GlobalStatistics globalStatistics = statisticsService.generateStatistics(conversation);

                globalStatistics.saveObjectGraph(ds);

                if (globalStatistics.getId() != null){
                    mailService.moveToFolder(conversation.getOriginalMessage(), inboxName, processedFolder);
                    mailService.sendMail(conversation.getSubmittedBy(), "Here are yor statistics", "http://127.0.0.1:4200/results/" + globalStatistics.getId().toHexString());
                }
            }else {
                logger.error("Parsing failed for message", conversation.getOriginalMessage());
                mailService.moveToFolder(conversation.getOriginalMessage(), inboxName, unprocessableFolder);
            }
        }
    }

    @Override
    public void start() {
        this.mailService.start();
        this.parserService.addObserver(this);
        this.parserService.start();
        this.statisticsService.start();
        this.statisticsService.addObserver(this);
    }

    @Override
    public void stop() {
        this.mailService.stop();
        this.parserService.stop();
        this.statisticsService.stop();
    }
}