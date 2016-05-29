package com.whatistics.backend.parser.language;

import com.google.inject.Singleton;
import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Language detector based on the language-detector library.
 * Refer to the <a href="https://github.com/optimaize/language-detector">GitHub repository</a> for more information.
 * Threadsafe
 */
@Singleton
public class LanguageDetectorOptimaize implements LanguageDetector {
    private final Logger logger = LoggerFactory.getLogger(LanguageDetectorOptimaize.class);

    private List<LanguageProfile> languageProfiles;
    private com.optimaize.langdetect.LanguageDetector languageDetector;
    private TextObjectFactory textObjectFactory;


    @Override
    public Optional<String> detectLanguage(String inputText) {
        lazyInitialize();

        //query:
        TextObject textObject = this.textObjectFactory.forText(inputText);
        com.google.common.base.Optional<LdLocale> lang = this.languageDetector.detect(textObject);

        return Optional.of(lang.get().getLanguage());
    }

    @Override
    public ProbabilisticLocale detectVagueLanguage(String inputText) {
        lazyInitialize();

        //query:
        TextObject textObject = this.textObjectFactory.forText(inputText);
        List<DetectedLanguage> detectedLanguages = this.languageDetector.getProbabilities(textObject);

        return new ProbabilisticLocale(detectedLanguages.get(0).getLocale().getLanguage(),
                detectedLanguages.get(0).getProbability());
    }

    private void lazyInitialize() {
        // lazy initialization
        if (languageDetector == null) {
            //load all languages:
            try {
                // read built-in profiles
                this.languageProfiles = new LanguageProfileReader().readAllBuiltIn();

                // read custom profiles
                // according to the documentation LanguageProfileReader#readAll should not be used for files within the .jar.
                List<String> profileFileNames = new ArrayList<>();
                Files.walk(Paths.get(this.getClass().getResource("/languageProfiles").toURI()))
                        .forEach(file -> {
                            if (!Files.isDirectory(file))
                                profileFileNames.add(file.getFileName().toString());
                        });

                this.languageProfiles.addAll(new LanguageProfileReader().read("/languageProfiles", profileFileNames));
            } catch (IOException | URISyntaxException e) {
                logger.error("Error loading language profiles", e);
            }

            this.languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard()).withProfiles(languageProfiles).build();

            this.textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
        }
    }
}
