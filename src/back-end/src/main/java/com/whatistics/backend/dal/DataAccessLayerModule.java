package com.whatistics.backend.dal;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.whatistics.backend.configuration.GlobalConfig;
import com.whatistics.backend.configuration.LocalConfig;

/**
 * Created by robert on 14/06/15.
 */
public class DataAccessLayerModule extends AbstractModule {

    @Override
    protected void configure() {
        bindConstant()
                .annotatedWith(Names.named("hostname"))
                .to(LocalConfig.MONGO_CLIENT_HOSTNAME);
        bindConstant()
                .annotatedWith(Names.named("database"))
                .to(LocalConfig.DB_NAME);
    }
}
