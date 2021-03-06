package com.whatistics.backend.statistics;

import com.google.inject.Inject;
import com.whatistics.backend.Service;
import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.GlobalStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;

/**
 * @author robert
 */
public class StatisticsService extends Observable implements Service {
    private final Logger logger = LoggerFactory.getLogger(StatisticsService.class);

    private StatisticsWorker statisticsWorker;

    @Inject
    public StatisticsService(StatisticsWorker statisticsWorker){
        this.statisticsWorker = statisticsWorker;
    }

    @Override
    public void start() {
        logger.info("service started");
    }

    @Override
    public void stop() {
        logger.info("service stopped");
    }

    public GlobalStatistics generateStatistics(Conversation conversation){
        return statisticsWorker.compute(conversation);
    }
}
