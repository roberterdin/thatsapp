package com.whatistics.backend;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.whatistics.backend.configuration.TypedProperties;
import com.whatistics.backend.dal.DataAccessLayerModule;
import com.whatistics.backend.mail.MailModule;
import com.whatistics.backend.parser.ParserModule;
import com.whatistics.backend.statistics.StatisticsModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;

/**
 * Main Whatistics class
 */
public class WhatisticsBackend {

    private static Injector injector;
    public static Random rand = new Random();
    public static TypedProperties globalProperties = new TypedProperties();
    public static TypedProperties passwordProperties = new TypedProperties();

    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(WhatisticsBackend.class);

        try {
            globalProperties.load(new FileReader("build/resources/main/global.properties"));
            passwordProperties.load(new FileReader("build/resources/main/password.properties"));
        } catch (IOException e) {
            logger.error("Error loading properties file", e);
        }


        injector = Guice.createInjector(new MailModule(),
                new ParserModule(),
                new DataAccessLayerModule(),
                new WhatisticsModule(),
                new StatisticsModule());


        WhatisticsService whatisticsService = injector.getInstance(WhatisticsService.class);
        whatisticsService.start();

    }

    public static Injector getInjector() {
        return injector;
    }

}