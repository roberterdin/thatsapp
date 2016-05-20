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

/**
 * Main Whatistics class
 */
public class WhatisticsBackend {

    private static Injector injector;
    public static Random rand = new Random();
    public static TypedProperties globalProperties = new TypedProperties();

    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(WhatisticsBackend.class);

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
                logger.error("Invalid command line parameter:" + args[0]);
                System.exit(-1);
            }
        } catch (IOException e) {
            logger.error("Error reading properties files", e);
            System.exit(-1);
        }


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
