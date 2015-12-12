package com.whatistics.backend.statistics;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.GlobalStatistics;
import com.whatistics.backend.model.Message;
import com.whatistics.backend.model.TimeInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author robert
 * Class should be threadsafe (not tested)
 */
public class StatisticsWorker {

    private final Logger logger = LoggerFactory.getLogger(StatisticsWorker.class);

    private final int statisticsLength;

    private final Pattern mediaPattern;
    private final Pattern emojiPattern;
    private final Pattern cleaningPattern;

    private final DateTimeFormatter sameDayChecker = DateTimeFormatter.ofPattern("yyyyMMdd");


    @Inject
    public StatisticsWorker(MediaPatternProvider mediaPatternProvider,
                            EmojiPatternProvider emojiPatternProvider,
                            @Named("statisticsLength") int statisticsLength){
        this.statisticsLength = statisticsLength;
        this.mediaPattern = mediaPatternProvider.get();
        this.emojiPattern = emojiPatternProvider.get();

        /* (\p{Punct}+) --> punctuation
         * ((?<=\s)\s+) --> all whitespaces following a whitespace
         * (^\s+|\s+$) --> leading and trailing whitespaces
         */
        this.cleaningPattern = Pattern.compile("((\\p{Punct}+)|((?<=\\s)\\s+)|(^\\s+|\\s+$))", Pattern.UNICODE_CHARACTER_CLASS);
    }

    public GlobalStatistics compute(Conversation conversation){
        long startTime = System.nanoTime();

        GlobalStatistics globalStatistics = new GlobalStatistics(conversation);

        int messagesPerDay = 0;
        Message prevMessage = null;
        for (Message message : conversation.getMessages()) {
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
            if(prevMessage != null && message.getSendDate().format(sameDayChecker).equals(prevMessage.getSendDate().format(sameDayChecker))){
                // still same day
                messagesPerDay++;
            }else {
                if(prevMessage != null){ // skip first day
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
     * @param globalStatistics
     * @param message
     */
    private void wordCountEmojiAndVocab(GlobalStatistics globalStatistics, Message message) {
        String cleanMessage = this.cleaningPattern.matcher(message.getContent()).replaceAll("");

        int wordCount = 0;
        if (!cleanMessage.isEmpty()) {
            String[] possibleWords = cleanMessage.split("\\s+");

            for (String token : possibleWords) {
                ExtractEmojiResult emojiResult = extractEmoji(token);
                if (emojiResult.emojis.size() > 0) {

                    for (String emoji : emojiResult.emojis) {
                        globalStatistics.getStatistics().incrementEmoji(emoji);
                        message.getSender().getStatistics().incrementEmoji(emoji);
                    }

                    // check if it was a combination of emojis and a word
                    if(emojiResult.wordCarryover.length() > 0){
                        globalStatistics.getStatistics().incrementVocuabulary(emojiResult.wordCarryover);
                        message.getSender().getStatistics().incrementVocuabulary(emojiResult.wordCarryover);
                        wordCount++;
                    }

                } else if (emojiResult.emojis.size() == 0) {
                    // it's (hopefully) a word.
                    globalStatistics.getStatistics().incrementVocuabulary(token);
                    message.getSender().getStatistics().incrementVocuabulary(token);
                    wordCount++;
                }

                globalStatistics.getStatistics().incrementWordAmount(wordCount);
                message.getSender().getStatistics().incrementWordAmount(wordCount);
            }
        }
    }

    /**
     * Checks whether the provided word is an emoji
     * Might also catch non-emojis if non-latin alphabets are used. In this case, have a look <a href="http://stackoverflow.com/a/24841069/695457">here</a>. Also have a look at the emoji csv in the resources folder in the root of the project.
     * @param possibleWord
     * @return
     */
    private ExtractEmojiResult extractEmoji(String possibleWord){
        try {

            ExtractEmojiResult result = new ExtractEmojiResult();

            byte[] utf8 = possibleWord.getBytes("UTF-8");
            // there are two bits for each character here
            String token = new String(utf8, "UTF-8");
            Matcher emojiMatcher = emojiPattern.matcher(token);

            result.emojis = new ArrayList<>();

            while (emojiMatcher.find()){
                result.emojis.add(emojiMatcher.group());
            }

            // carry over possible characters, e.g. from cake🍹
            if (result.emojis.size() > 0 ){
                result.wordCarryover = emojiMatcher.replaceAll(""); //cake? --> cake
            }else {
                result.wordCarryover = "";
            }

            return result;

        } catch (UnsupportedEncodingException e) {
            logger.error("Encoding error while checking for emoji", e);
            return null;
        }
    }

    /**
     * Result tuple for {@link StatisticsWorker#extractEmoji(String)}
     */
    private static class ExtractEmojiResult{
        String wordCarryover;
        List<String> emojis;
    }
}
