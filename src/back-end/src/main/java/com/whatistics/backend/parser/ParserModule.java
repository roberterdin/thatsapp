package com.whatistics.backend.parser;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.whatistics.backend.WhatisticsBackend;

/**
 * @author Robert
 */
public class ParserModule extends AbstractModule {
    @Override
    protected void configure() {

        Names.bindProperties(binder(), WhatisticsBackend.globalProperties);

//        bindConstant()
//                .annotatedWith(Names.named("parsers"))
//                .to(GlobalConfig.NO_OF_PARSERS);
//
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


        // component binding
        bind(ParserService.class).to(ParserServiceImpl.class);
    }
}
