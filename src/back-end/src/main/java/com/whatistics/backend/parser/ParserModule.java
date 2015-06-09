package com.whatistics.backend.parser;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.whatistics.backend.configuration.GlobalConfig;

/**
 * @author Robert
 */
public class ParserModule extends AbstractModule {
    @Override
    protected void configure() {

        bindConstant()
                .annotatedWith(Names.named("parsers"))
                .to(GlobalConfig.NO_OF_PARSERS);

    }
}
