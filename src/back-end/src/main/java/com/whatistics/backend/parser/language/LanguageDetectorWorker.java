package com.whatistics.backend.parser.language;

import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by robert on 25/05/16.
 */
public class LanguageDetectorWorker implements Callable<Conversation> {

    private final Logger logger = LoggerFactory.getLogger(LanguageDetectorWorker.class);

    private final LanguageDetector languageDetector;
    private final Conversation conversation;

    public LanguageDetectorWorker(LanguageDetector languageDetector, Conversation conversation) {
        this.languageDetector = languageDetector;
        this.conversation = conversation;
    }


    @Override
    public Conversation call() {
        logger.debug("Language detector worker started...");
        long startTime = System.nanoTime();

        // todo monitor performance and do async if necessary
        String textToDetect = conversation.getMessages().stream()
                .sorted((o1, o2) -> {
                    if (o1.getContent().length() < o2.getContent().length()) {
                        return 1;
                    } else if (o1.getContent().length() > o2.getContent().length()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }).limit(100)
                .map(i -> i.getContent().toString())
                .collect(Collectors.joining("\n"));

        logger.debug("Concatenated text created: \n" + textToDetect);

        ProbabilisticLocale locale = this.languageDetector.detectVagueLanguage(textToDetect);

        conversation.setLanguage(locale.getLocale().getLanguage());
        if (locale.getLanguageProbability().isPresent())
            conversation.setLanguageProbability(locale.getLanguageProbability().get());

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        logger.info("Time to detect language: " + duration / 1000000 + "ms");

        return conversation;
    }
}
