package com.whatistics.backend;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.whatistics.backend.mail.MailModule;
import com.whatistics.backend.mail.MailService;
import com.whatistics.backend.parser.ParserModule;

/**
 * Main Whatistics class
 */
public class WhatisticsBackend {

  private static Injector injector;

  public static void main(String[] args) {
    System.out.println("Hello, World!");

    injector = Guice.createInjector(new MailModule(), new ParserModule());

    MailService mailService = injector.getInstance(MailService.class);
    mailService.start();

  }

  public static Injector getInjector(){
    return injector;
  }
}
