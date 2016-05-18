package com.whatistics.backend;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * @author robert
 */
public class WhatisticsModule extends AbstractModule {

    @Override
    protected void configure() {

        Names.bindProperties(binder(), WhatisticsBackend.globalProperties);

//        bindConstant()
//                .annotatedWith(Names.named("inboxName"))
//                .to(GlobalConfig.INBOX_NAME);
//
//        bindConstant()
//                .annotatedWith(Names.named("processedFolder"))
//                .to(GlobalConfig.PROCESSED_FOLDER);
//
//        bindConstant()
//                .annotatedWith(Names.named("unprocessableFolder"))
//                .to(GlobalConfig.UNPROCESSABLE_FOLDER);

    }


}