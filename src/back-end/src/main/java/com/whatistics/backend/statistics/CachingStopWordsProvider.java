package com.whatistics.backend.statistics;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Singleton
public class CachingStopWordsProvider implements StopWordsProvider {
    private final Logger logger = LoggerFactory.getLogger(CachingStopWordsProvider.class);

    private Map<String, Set<String>> setCache = new HashMap<>();
    private Map<String, Pattern> patternCache = new HashMap<>();

    @Override
    public Set<String> stopWordsFor(String language) {
        if(this.setCache.containsKey(language))
            return this.setCache.get(language);

        try {
            BufferedReader br = Files.newBufferedReader(Paths.get(this.getClass().getResource("/stopwords/" + language + ".txt").toURI()));
            this.setCache.put(language, br.lines().collect(Collectors.toSet()));
        } catch (IOException | URISyntaxException e) {
            this.logger.error("Unable to find given language: " + language + ". Will return an empty stop words set. ", e);

            this.setCache.put(language, new HashSet<>());
        }

        return this.setCache.get(language);
    }

    @Override
    public Pattern stopWordsPatternFor(String language) {

        if (!patternCache.containsKey(language)){
            StringBuilder stringBuilder = new StringBuilder("\\b(?:");
            stringBuilder.append(stopWordsFor(language).stream().collect(Collectors.joining("|")));
            stringBuilder.append(")\\b\\s*");
            patternCache.put(language, Pattern.compile(stringBuilder.toString(), Pattern.CASE_INSENSITIVE));
        }

        return patternCache.get(language);
    }
}
