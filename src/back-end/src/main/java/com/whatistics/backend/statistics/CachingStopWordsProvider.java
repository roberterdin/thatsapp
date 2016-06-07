package com.whatistics.backend.statistics;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Singleton
public class CachingStopWordsProvider implements StopWordsProvider {
    private final Logger logger = LoggerFactory.getLogger(CachingStopWordsProvider.class);

    private Map<String, Set<String>> rawCache = new HashMap<>();
    private Map<String, Pattern> patternCache = new HashMap<>();

    private final Pattern commentPattern = Pattern.compile("\\s*#.*");

    @Override
    public synchronized Set<String> stopWordsFor(String language) {
        if (this.rawCache.containsKey(language))
            return this.rawCache.get(language);

        try {
            // prevent FileSystemNotFoundException...
            final Map<String, String> env = new HashMap<>();
            final String[] array = this.getClass().getResource("/stopwords/" + language + ".txt").toURI().toString().split("!");

            FileSystem fs = null;
            Path path;
            if (array.length > 1) {
                fs = FileSystems.newFileSystem(URI.create(array[0]), env);
                path = fs.getPath(array[1]);
            } else {
                path = Paths.get(this.getClass().getResource("/stopwords/" + language + ".txt").toURI());
            }
            BufferedReader br = Files.newBufferedReader(path);
            this.rawCache.put(language, br.lines().filter(line -> !commentPattern.matcher(line).matches()).collect(Collectors.toSet()));
            if (fs != null)
                fs.close();
        } catch (IOException | URISyntaxException e) {
            this.logger.error("Unable to find given language: " + language + ". Will return an empty stop words set. ", e);

            this.rawCache.put(language, new HashSet<>());
        }

        return this.rawCache.get(language);
    }

    @Override
    public synchronized Pattern stopWordsPatternFor(String language) {

        if (!patternCache.containsKey(language)) {
            StringBuilder stringBuilder = new StringBuilder("\\b(?:");
            stringBuilder.append(stopWordsFor(language).stream().collect(Collectors.joining("|")));
            stringBuilder.append(")\\b\\s*");
            patternCache.put(language, Pattern.compile(stringBuilder.toString(), Pattern.CASE_INSENSITIVE));
        }

        return patternCache.get(language);
    }
}
