package com.whatistics.backend.statistics;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.whatistics.backend.WhatisticsBackend;

/**
 * @author robert
 */
public class StatisticsModule extends AbstractModule {
    @Override
    protected void configure() {

        Names.bindProperties(binder(), WhatisticsBackend.globalProperties);

//        bindConstant()
//                .annotatedWith(Names.named("statisticsLength"))
//                .to(GlobalConfig.STATISTICS_LENGTH);

    }
}
