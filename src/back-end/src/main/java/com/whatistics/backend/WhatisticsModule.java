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

    }


}