package com.whatistics.backend.parser;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.whatistics.backend.WhatisticsBackend;
import com.whatistics.backend.parser.language.LanguageDetector;
import com.whatistics.backend.parser.language.LanguageDetectorOptimaize;

/**
 * @author Robert
 */
public class ParserModule extends AbstractModule {
    @Override
    protected void configure() {

        Names.bindProperties(binder(), WhatisticsBackend.globalProperties);

        // component binding
        bind(ParserService.class).to(ParserServiceImpl.class);
        bind(LanguageDetector.class).to(LanguageDetectorOptimaize.class);
    }
}
