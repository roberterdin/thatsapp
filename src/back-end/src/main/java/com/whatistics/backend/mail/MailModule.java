package com.whatistics.backend.mail;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.whatistics.backend.configuration.GlobalConfig;

/**
 * Module configuration.
 */
public class MailModule extends AbstractModule {

    @Override
    protected void configure() {

        // configuration / constant binding

        bindConstant()
                .annotatedWith(Names.named("host"))
                .to(GlobalConfig.HOST);

        bindConstant()
                .annotatedWith(Names.named("email"))
                .to(GlobalConfig.EMAIL);

        bindConstant()
                .annotatedWith(Names.named("pass"))
                .to(GlobalConfig.PASSWORD);

        bindConstant()
                .annotatedWith(Names.named("inboxName"))
                .to(GlobalConfig.INBOX_NAME);

        bindConstant()
                .annotatedWith(Names.named("processedFolder"))
                .to(GlobalConfig.PROCESSED_FOLDER);

        bindConstant()
                .annotatedWith(Names.named("unprocessableFolder"))
                .to(GlobalConfig.UNPROCESSABLE_FOLDER);


        // component binding

        bind(MailAdapter.class).to(IMAPMailAdapter.class);

    }
}