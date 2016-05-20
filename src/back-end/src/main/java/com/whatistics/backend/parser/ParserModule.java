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

        // component binding
        bind(ParserService.class).to(ParserServiceImpl.class);
    }
}
