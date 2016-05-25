package com.whatistics.backend.parser.language;

import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.Message;

import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by robert on 25/05/16.
 */
public class LanguageDetectorWorker implements Callable<Conversation> {

    private final LanguageDetector languageDetector;
    private final Conversation conversation;

    public LanguageDetectorWorker(LanguageDetector languageDetector, Conversation conversation) {
        this.languageDetector = languageDetector;
        this.conversation = conversation;
    }


    @Override
    public Conversation call() {
        // todo monitor performance and do async if necessary
        String textToDetect = conversation.getMessages().stream()
                .sorted(new Comparator<Message>() {
                    @Override
                    public int compare(com.whatistics.backend.model.Message o1, com.whatistics.backend.model.Message o2) {
                        if (o1.getContent().length() < o2.getContent().length()) {
                            return 1;
                        } else if (o1.getContent().length() > o2.getContent().length()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                }).limit(100)
                .map(i -> i.getContent().toString())
                .collect(Collectors.joining("\n"));

        ProbabilisticLocale locale = this.languageDetector.detectVagueLanguage(textToDetect);

        conversation.setLanguage(locale.getLocale().getLanguage());
        if (locale.getLanguageProbability().isPresent())
            conversation.setLanguageProbability(locale.getLanguageProbability().get());

        return conversation;
    }
}
