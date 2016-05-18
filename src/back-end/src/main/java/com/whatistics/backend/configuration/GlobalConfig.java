package com.whatistics.backend.configuration;

/**
 * @author robert
 * TODO: move to properties file, load into Guice modules like described <a href="http://stackoverflow.com/questions/4805874/guice-and-general-application-configuration">here</a>}
 * @deprecated
 */
public class GlobalConfig {

    // Account settings
    public static final String HOST = "imap.gmail.com";
    public static final String EMAIL = "whatistics@gmail.com";
    public static final String PASSWORD = "#uu^b4{fe-XS-!Z";

    // Mailbox settings
    public static final String INBOX_NAME = "Inbox";
    public static final String PROCESSED_FOLDER = "processed";
    public static final String UNPROCESSABLE_FOLDER = "unprocessable";

    // Performance settings
    public static final int NO_OF_PARSERS = 1;
    public static final int MAX_CONCURRENT_MAIL_FETCHER_TASKS = 1;
    public static final int MAIL_FETCHING_INTERVAL = 5; // seconds
    public static final int STATISTICS_LENGTH = Integer.MAX_VALUE;
}