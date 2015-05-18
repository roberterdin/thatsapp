package com.whatistics.backend.configuration;

import com.mongodb.MongoClient;

/**
 * Created by robert on 18/05/15.
 */
public class LocalConfig {

    public static String DB_NAME = "whatistics";
    public static MongoClient MONGO_CLIENT = new MongoClient( "localhost" );

}
