package com.whatistics.backend.rest;

import com.whatistics.backend.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Starts an embedded REST service to allow access to the database.
 * @author robert
 */
public class RestService implements Service {

    private final Logger logger = LoggerFactory.getLogger(RestService.class);
    private Process restProc = null;


    @Override
    public void start() {
        // start the restheart server as a process
        // todo: redirect stdout or call lib directly
        try {
            logger.info("Trying to start RESTHeart as a sub-process");
            restProc = Runtime.getRuntime().exec("java -server -jar build/libs/lib/restheart-1.1.7.jar");
        } catch (IOException e) {
            logger.error("Failed starting RESTHeart Server", e);
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        logger.info("Destroying down RESTHeart sub-process");
        restProc.destroyForcibly();
    }
}
