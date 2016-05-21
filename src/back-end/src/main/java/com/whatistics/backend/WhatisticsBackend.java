package com.whatistics.backend;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.whatistics.backend.configuration.TypedProperties;
import com.whatistics.backend.dal.DataAccessLayerModule;
import com.whatistics.backend.mail.MailModule;
import com.whatistics.backend.parser.ParserModule;
import com.whatistics.backend.rest.RestModule;
import com.whatistics.backend.statistics.StatisticsModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.SynchronousQueue;

/**
 * Main Whatistics class
 */
public class WhatisticsBackend {

    private static Injector injector;
    public static Random rand = new Random();
    public static TypedProperties globalProperties = new TypedProperties();

    public static void main(String[] args) {
        try {
            globalProperties.load(WhatisticsBackend.class.getClassLoader().getResourceAsStream("global.properties"));
            globalProperties.load(WhatisticsBackend.class.getClassLoader().getResourceAsStream("password.properties"));

            // read environment
            if (args.length == 0 || args[0].equals("dev")) {
                // fall back to dev
                globalProperties.load(WhatisticsBackend.class.getClassLoader().getResourceAsStream("dev.properties"));
            } else if (args[0].equals("prod") || args[0].equals("test")) {
                globalProperties.load(WhatisticsBackend.class.getClassLoader().getResourceAsStream("test.properties"));
            } else {
                System.err.println("Invalid command line parameter:" + args[0]);
                System.exit(-1);
            }
        } catch (IOException e) {
            System.err.println("Error reading properties files");
            e.printStackTrace();
            System.exit(-1);
        }


        //initialize logger
        System.getProperties().setProperty("org.slf4j.simpleLogger.logFile", globalProperties.getProperty("logFile"));
        final Logger logger = LoggerFactory.getLogger(WhatisticsBackend.class);


        injector = Guice.createInjector(new MailModule(),
                new ParserModule(),
                new DataAccessLayerModule(),
                new WhatisticsModule(),
                new RestModule(),
                new StatisticsModule());


        WhatisticsService whatisticsService = injector.getInstance(WhatisticsService.class);
        whatisticsService.start();

    }

    public static Injector getInjector() {
        return injector;
    }

}
