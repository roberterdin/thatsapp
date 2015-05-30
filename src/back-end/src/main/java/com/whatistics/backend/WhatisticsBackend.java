package com.whatistics.backend;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.whatistics.backend.mail.MailFetcherTask;
import com.whatistics.backend.mail.MailModule;

/**
 * Main Whatistics class
 */
public class WhatisticsBackend {

  private static Injector injector;

  public static void main(String[] args) {
    System.out.println("Hello, World!");

    injector = Guice.createInjector(new MailModule());

    MailFetcherTask mailFetcherTask = injector.getInstance(MailFetcherTask.class);
    mailFetcherTask.run();
  }

  public static Injector getInjector(){
    return injector;
  }
}
