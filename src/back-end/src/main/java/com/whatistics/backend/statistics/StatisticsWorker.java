package com.whatistics.backend.statistics;

import com.google.inject.Inject;
import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.GlobalStatistics;
import com.whatistics.backend.model.Message;

import java.util.regex.Pattern;


/**
 * @author robert
 */
public class StatisticsWorker {

    private Pattern mediaPattern;

    @Inject
    public StatisticsWorker(MediaPatternProvider mediaPatternProvider){
        this.mediaPattern = mediaPatternProvider.get();
    }

    public GlobalStatistics compute(Conversation conversation){

        GlobalStatistics globalStatistics = new GlobalStatistics(conversation);

        for (Message message : conversation.getMessages()){

            // Increment message count
            globalStatistics.getStatistics().incrementMessageAmount();
            globalStatistics.getPersonalStats(message.getSender()).incrementMessageAmount();

            // Increment media count


            // Increment word count


        }




        return globalStatistics;
    }
}
