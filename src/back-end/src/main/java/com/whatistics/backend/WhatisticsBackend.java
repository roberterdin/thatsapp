package com.whatistics.backend;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.whatistics.backend.configuration.LocalConfig;
import com.whatistics.backend.dal.DataAccessLayerModule;
import com.whatistics.backend.mail.MailModule;
import com.whatistics.backend.mail.MailService;
import com.whatistics.backend.parser.ParserModule;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Whatistics class
 */
public class WhatisticsBackend {

  private static Injector injector;

  public static void main(String[] args) {
//    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");

    final Logger logger = LoggerFactory.getLogger(WhatisticsBackend.class);


    logger.debug("Hello, Woorld!");

    injector = Guice.createInjector(new MailModule(),
            new ParserModule(),
            new DataAccessLayerModule(),
            new WhatisticsModule());

    WhatisticsService whatisticsService = injector.getInstance(WhatisticsService.class);
    whatisticsService.start();

  }

  public static Injector getInjector(){
    return injector;
  }

}