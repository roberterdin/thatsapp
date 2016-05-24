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

        // bind password property

        bindConstant()
                .annotatedWith(Names.named("password"))
                .to(WhatisticsBackend.globalProperties.getProperty(WhatisticsBackend.globalProperties.getProperty("password.property")));

        // component binding
        bind(MailAdapter.class).to(IMAPMailAdapter.class);

    }
}
