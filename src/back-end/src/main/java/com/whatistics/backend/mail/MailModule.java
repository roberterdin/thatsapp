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


        // component binding

        bind(MailAdapterService.class).to(IMAPMailAdapterService.class);

    }
}
