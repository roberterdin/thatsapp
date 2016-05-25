package com.whatistics.backend.parser.language;

import java.util.Optional;

/**
 * Detects languages ;-)
 * ISO 639-1 language code if there is one, otherwise the ISO 639-3 code.
 */
public interface LanguageDetector {

    /**
     * Will not return a language if the probability is too low.
     * @param inputText
     * @return
     */
    public Optional<String> detectLanguage(String inputText);

    /**
     * Will always return a result. Result might be of bad quality.
     * @param inputText
     * @return
     */
    public ProbabilisticLocale detectVagueLanguage(String inputText);

}
