package com.whatistics.backend;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.whatistics.backend.dal.DataAccessLayerModule;
import com.whatistics.backend.mail.MailModule;
import com.whatistics.backend.parser.ParserModule;
import com.whatistics.backend.statistics.StatisticsModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;

/**
 * Main Whatistics class
 */
public class WhatisticsBackend {

  private static Injector injector;

  public static Random rand = new Random();

  public static void main(String[] args) {
//    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");

    final Logger logger = LoggerFactory.getLogger(WhatisticsBackend.class);


    injector = Guice.createInjector(new MailModule(),
            new ParserModule(),
            new DataAccessLayerModule(),
            new WhatisticsModule(),
            new StatisticsModule());



    // start the restheart server as a process
    // todo: make RESTHeart use configuration file from resources
    // todo: redirect stdout or call lib directly
    try {
      Process restProc = Runtime.getRuntime().exec("java -server -jar build/libs/lib/restheart-1.1.7.jar");
    } catch (IOException e) {
      logger.error("Failed starting RESTHeart Server", e);
      e.printStackTrace();
    }


    WhatisticsService whatisticsService = injector.getInstance(WhatisticsService.class);
    whatisticsService.start();

  }

  public static Injector getInjector(){
    return injector;
  }

}