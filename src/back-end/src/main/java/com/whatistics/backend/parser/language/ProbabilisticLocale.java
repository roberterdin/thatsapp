package com.whatistics.backend.parser.language;

import java.util.Locale;
import java.util.Optional;

/**
 * Include the probability that locale parameters are correct. Necessary because locales are detected based on the conversation content.
 */
public class ProbabilisticLocale {
    private final Locale locale;
    private Optional<Double> languageProbability = Optional.empty();
    private Optional<Double> countryProbability = Optional.empty();
    private Optional<Double> variantProbability = Optional.empty();

    public ProbabilisticLocale(String language) {
        this.locale = new Locale(language);
    }

    public ProbabilisticLocale(String language, double probability){
        this(language);
        this.languageProbability = Optional.of(probability);
    }

    public Locale getLocale() {
        return locale;
    }

    public Optional<Double> getLanguageProbability() {
        return languageProbability;
    }

    public Optional<Double> getCountryProbability() {
        return countryProbability;
    }

    public Optional<Double> getVariantProbability() {
        return variantProbability;
    }
}
