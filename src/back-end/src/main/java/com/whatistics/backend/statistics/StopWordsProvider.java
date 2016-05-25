package com.whatistics.backend.statistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by robert on 25/05/16.
 */
public interface StopWordsProvider {
    public Set<String> stopWordsFor(String language);
    public Pattern stopWordsPatternFor(String language);
}
