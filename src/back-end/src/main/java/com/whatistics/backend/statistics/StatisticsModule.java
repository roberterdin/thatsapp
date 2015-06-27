package com.whatistics.backend.statistics;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.whatistics.backend.configuration.GlobalConfig;

/**
 * @author robert
 */
public class StatisticsModule extends AbstractModule {
    @Override
    protected void configure() {

        bindConstant()
                .annotatedWith(Names.named("statisticsLength"))
                .to(GlobalConfig.STATISTICS_LENGTH);

    }
}
