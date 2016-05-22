package com.whatistics.backend.statistics;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.vdurmont.emoji.EmojiParser;
import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.GlobalStatistics;
import com.whatistics.backend.model.Message;
import com.whatistics.backend.model.TimeInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author robert
 *         Class should be threadsafe (not tested)
 */
public class StatisticsWorker {

    private final Logger logger = LoggerFactory.getLogger(StatisticsWorker.class);

    private final int statisticsLength;

    private final Pattern mediaPattern;
    private final Pattern cleaningPattern;

    private final DateTimeFormatter sameDayChecker = DateTimeFormatter.ofPattern("yyyyMMdd");


    @Inject
    public StatisticsWorker(MediaPatternProvider mediaPatternProvider,
                            @Named("statisticsLength") int statisticsLength) {
        this.statisticsLength = statisticsLength;
        this.mediaPattern = mediaPatternProvider.get();

        /* (\p{Punct}+) --> punctuation
         * ((?<=\s)\s+) --> all whitespaces following a whitespace
         * (^\s+|\s+$) --> leading and trailing whitespaces
         */
        this.cleaningPattern = Pattern.compile("((\\p{Punct}+)|((?<=\\s)\\s+)|(^\\s+|\\s+$))", Pattern.UNICODE_CHARACTER_CLASS);
    }

    public GlobalStatistics compute(Conversation conversation) {
        long startTime = System.nanoTime();

        GlobalStatistics globalStatistics = new GlobalStatistics(conversation);

        int messagesPerDay = 0;
        Message prevMessage = null;
        for (Message message : conversation.getMessages()) {
            // Check if system message, discard if so
            if (message.getSender().getName().equals("_dummy"))
                continue;

            // Increment message count
            //-------------
            globalStatistics.getStatistics().incrementMessageAmount();
            message.getSender().getStatistics().incrementMessageAmount();

            // Increment media count
            //-------------
            if (mediaPattern.matcher(message.getContent()).matches()) {
                globalStatistics.getStatistics().incrementMediaAmount();
                message.getSender().getStatistics().incrementMediaAmount();
                continue;
            }

            // Increment word count, emojis and vocabulary
            //-------------
            wordCountEmojiAndVocab(globalStatistics, message);

            // aggregatedHistory
            //-------------
            if (prevMessage != null && message.getSendDate().format(sameDayChecker).equals(prevMessage.getSendDate().format(sameDayChecker))) {
                // still same day
                messagesPerDay++;
            } else {
                if (prevMessage != null) { // skip first day
                    // new day
                    LocalDateTime yesterday = message.getSendDate().minusDays(1);

                    TimeInterval currentDay = TimeInterval.createDay(yesterday);
                    currentDay.getStatistics().setMessageAmount(messagesPerDay);

                    globalStatistics.getAggregatedHistory().add(currentDay);
                }
                messagesPerDay = 1;
            }


            prevMessage = message;
        }

        // put last message into aggregated history
        LocalDateTime yesterdayDate = prevMessage.getSendDate().minusDays(1);
        TimeInterval yesterday = TimeInterval.createDay(yesterdayDate);
        yesterday.getStatistics().setMessageAmount(messagesPerDay);
        globalStatistics.getAggregatedHistory().add(yesterday);

        // remove system messages
        // todo: find a way to remove without breaking the db
//        globalStatistics.getParticipants().removeIf(p -> p.getName().equals("_dummy"));

        // get things in order
        globalStatistics.inflateAggregatedHistory();

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        logger.info("Time to generate statistics: " + duration / 1000000 + "ms");
        return globalStatistics;
    }

    /**
     * Counts the amount of words and emojis. The words get added to the vocabulary.
     * Grouped together for performance reasons.
     *
     * @param globalStatistics
     * @param message
     */
    private void wordCountEmojiAndVocab(GlobalStatistics globalStatistics, Message message) {
        String cleanMessage = this.cleaningPattern.matcher(message.getContent()).replaceAll("");

        List<EmojiParser.UnicodeCandidate> emojis = EmojiParser.getUnicodeCandidates(message.getContent());

        for (EmojiParser.UnicodeCandidate uc : emojis) {
            globalStatistics.getStatistics().incrementEmoji(uc.getEmoji().getUnicode());
            message.getSender().getStatistics().incrementEmoji(uc.getEmoji().getUnicode());
        }

        cleanMessage = EmojiParser.removeAllEmojis(cleanMessage);

        if (!cleanMessage.isEmpty()) {
            String[] possibleWords = cleanMessage.split("\\s+");
            globalStatistics.getStatistics().incrementWordAmount(possibleWords.length);
            message.getSender().getStatistics().incrementWordAmount(possibleWords.length);

            for (String token : possibleWords) {
                globalStatistics.getStatistics().incrementVocabulary(token);
                message.getSender().getStatistics().incrementVocabulary(token);
            }
        }
    }
}
