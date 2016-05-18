package com.whatistics.backend.dal;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.whatistics.backend.WhatisticsBackend;

/**
 * Created by robert on 14/06/15.
 */
public class DataAccessLayerModule extends AbstractModule {

    @Override
    protected void configure() {
        Names.bindProperties(binder(), WhatisticsBackend.globalProperties);
    }
}
