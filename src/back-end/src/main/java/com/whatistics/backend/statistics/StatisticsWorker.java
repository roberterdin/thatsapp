package com.whatistics.backend.statistics;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.GlobalStatistics;
import com.whatistics.backend.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

    private Pattern mediaPattern;
    private Pattern emojiPattern;
    private Pattern cleaningPattern;


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

        GlobalStatistics globalStatistics = new GlobalStatistics(conversation);

        for (Message message : conversation.getMessages()) {
            // Increment message count
            globalStatistics.getStatistics().incrementMessageAmount();
            globalStatistics.getPersonalStats(message.getSender()).incrementMessageAmount();

            // Increment media count
            if (mediaPattern.matcher(message.getContent()).matches()) {
                globalStatistics.getStatistics().incrementMediaAmount();
                globalStatistics.getPersonalStats(message.getSender()).incrementMediaAmount();
                continue;
            }

            // Increment word count, emojis and vocabulary
            // strip punctuaiton
            String cleanMessage = this.cleaningPattern.matcher(message.getContent()).replaceAll("");
            // TODO: include into cleanMessage pattern
            cleanMessage = cleanMessage.trim();
            int wordCount = 0;
            if (!cleanMessage.isEmpty()) {
                String[] possibleWords = message.getContent().split("\\s+");

                for (String token : possibleWords) {
                    ExtractEmojiResult emojiResult = extractEmoji(token);
                    if (emojiResult.emojis.size() > 0) {
                        // there are emojis... yaay!
                        for (String emoji : emojiResult.emojis) {
                            globalStatistics.getStatistics().incrementEmoji(emoji);
                            globalStatistics.getPersonalStats(message.getSender()).incrementEmoji(emoji);
                            logger.debug("Emoji found: " + token);
                        }

                        // check if it was a combination of emojis and a word
                        if(emojiResult.wordCarryover.length() > 0){
                            globalStatistics.getStatistics().incrementVocuabulary(emojiResult.wordCarryover);
                            globalStatistics.getPersonalStats(message.getSender()).incrementVocuabulary(emojiResult.wordCarryover);
                            wordCount++;
                        }

                    } else if (emojiResult.emojis.size() == 0) {
                        // it's (hopefully) a word.
                        globalStatistics.getStatistics().incrementVocuabulary(token);
                        globalStatistics.getPersonalStats(message.getSender()).incrementVocuabulary(token);
                        wordCount++;
                    }

                    globalStatistics.getStatistics().incrementWordAmount(wordCount);
                    globalStatistics.getPersonalStats(message.getSender()).incrementWordAmount(wordCount);
                }
            }
        }

        globalStatistics.sort(Integer.MAX_VALUE);
        return globalStatistics;
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

    private static class ExtractEmojiResult{
        String wordCarryover;
        List<String> emojis;
    }
}