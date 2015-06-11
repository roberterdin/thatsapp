package com.whatistics.backend;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.whatistics.backend.mail.MailModule;
import com.whatistics.backend.mail.MailService;
import com.whatistics.backend.parser.ParserModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Main Whatistics class
 */
public class WhatisticsBackend {

  private static Injector injector;

  public static void main(String[] args) {
//    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");

    final  Logger logger = LoggerFactory.getLogger(WhatisticsBackend.class);

    logger.debug("Hello, Woorld!");

    injector = Guice.createInjector(new MailModule(), new ParserModule());

    MailService mailService = injector.getInstance(MailService.class);
    mailService.start();

  }

  public static Injector getInjector(){
    return injector;
  }
}