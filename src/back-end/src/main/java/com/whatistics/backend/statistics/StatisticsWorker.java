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
 * TODO: clean up, a lot of chaos due to global declarations. Not sure if this really brings more performance.
 */
public class StatisticsWorker {

    private final Logger logger = LoggerFactory.getLogger(StatisticsWorker.class);

    private final int statisticsLength;

    private Pattern mediaPattern;
    private Pattern emojiPattern;

    // temp variables to prevent allocation for every message or even overy word
    private byte[] utf8 = new byte[0];
    private String token;
    private Matcher emojiMatcher;
    private String wordCarryover;


    @Inject
    public StatisticsWorker(MediaPatternProvider mediaPatternProvider,
                            EmojiPatternProvider emojiPatternProvider,
                            @Named("statisticsLength") int statisticsLength){
        this.statisticsLength = statisticsLength;
        this.mediaPattern = mediaPatternProvider.get();
        this.emojiPattern = emojiPatternProvider.get();
    }

    public GlobalStatistics compute(Conversation conversation){

        // reusable temporary variables to prevent memory allocation with each iteration
        int wordCount;
        List<String> tmpEmojis = new ArrayList<>();
        String cleanMessage;

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
            cleanMessage = message.getContent().replaceAll("\\p{Punct}+", "");
            cleanMessage = message.getContent().trim();
            wordCount = 0;
            if (!cleanMessage.isEmpty()) {
                String[] possibleWords = message.getContent().split("\\s+");

                for (String token : possibleWords) {
                    tmpEmojis = extractEmoji(token);
                    if (tmpEmojis.size() > 0) {
                        // there are emojis... yaay!
                        for (String emoji : tmpEmojis) {
                            globalStatistics.getStatistics().incrementEmoji(emoji);
                            globalStatistics.getPersonalStats(message.getSender()).incrementEmoji(emoji);
                            logger.debug("Emoji found: " + token);
                        }

                        // check if it was a combination of emojis and a word
                        if(this.wordCarryover.length() > 0){
                            globalStatistics.getStatistics().incrementVocuabulary(this.wordCarryover);
                            globalStatistics.getPersonalStats(message.getSender()).incrementVocuabulary(this.wordCarryover);
                            wordCount++;
                        }

                    } else if (tmpEmojis.size() == 0) {
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
    private List<String> extractEmoji(String possibleWord){
        try {
            this.utf8 = possibleWord.getBytes("UTF-8");

            // there are two bits for each character here
            this.token = new String(utf8, "UTF-8");
            this.emojiMatcher = emojiPattern.matcher(token);

            List<String> result = new ArrayList<>();

            while (emojiMatcher.find()){
                result.add(emojiMatcher.group());
            }

            // carry over possible characters, e.g. from cake🍹
            if (result.size() > 0 ){
                this.wordCarryover = emojiMatcher.replaceAll(""); //cake? --> cake
            }else {
                this.wordCarryover = "";
            }

            return result;

        } catch (UnsupportedEncodingException e) {
            logger.error("Encoding error while checking for emoji", e);
            return null;
        }
    }
}
