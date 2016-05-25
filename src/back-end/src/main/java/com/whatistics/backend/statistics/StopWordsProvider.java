package com.whatistics.backend.statistics;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by robert on 25/05/16.
 */
public interface StopWordsProvider {
    public Set<String> stopWordsFor(String language);
    public Pattern stopWordsPatternFor(String language);
}
