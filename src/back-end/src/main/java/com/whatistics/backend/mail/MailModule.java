package com.whatistics.backend.mail;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.whatistics.backend.WhatisticsBackend;

/**
 * Module configuration.
 */
public class MailModule extends AbstractModule {

    @Override
    protected void configure() {

        // configuration / constant binding
        Names.bindProperties(binder(), WhatisticsBackend.globalProperties);
        Names.bindProperties(binder(), WhatisticsBackend.passwordProperties);


//        bindConstant()
//                .annotatedWith(Names.named("maxMailFetcherAmount"))
//                .to(GlobalConfig.MAX_CONCURRENT_MAIL_FETCHER_TASKS);
//
//        bindConstant()
//                .annotatedWith(Names.named("mailFetchingInterval"))
//                .to(GlobalConfig.MAIL_FETCHING_INTERVAL);
//
//        bindConstant()
//                .annotatedWith(Names.named("host"))
//                .to(GlobalConfig.HOST);
//
//        bindConstant()
//                .annotatedWith(Names.named("email"))
//                .to(GlobalConfig.EMAIL);
//
//        bindConstant()
//                .annotatedWith(Names.named("pass"))
//                .to(GlobalConfig.PASSWORD);
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

        bind(MailAdapter.class).to(IMAPMailAdapter.class);

    }
}