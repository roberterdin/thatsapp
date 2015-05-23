package com.whatistics.backend;

import com.whatistics.backend.tasks.MailFetcherTask;

/**
 * Main Whatistics class
 */
public class WhatisticsBackend {
  public static void main(String[] args) {
    System.out.println("Hello, World!");

    MailFetcherTask mailFetcherTask = new MailFetcherTask();
    mailFetcherTask.run();
  }
}
