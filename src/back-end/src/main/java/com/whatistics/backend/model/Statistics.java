package com.whatistics.backend.model;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Implement trim to reduce vocabulary size. Vocabulary can always be recomputed from the conversation.
 * @author robert
 */
public class Statistics {

    @Id
    private ObjectId id;

    private int wordAmount = 0;
    private int messageAmount = 0;
    private int mediaAmount = 0;

    private Map<String, Integer> vocabulary = new HashMap<>();
    private Map<String, Integer> emoticons = new HashMap<>();

    public Statistics(){

    }

    public int getWordAmount() {
        return wordAmount;
    }

    public void incrementWordAmount(){
        wordAmount++;
    }

    public int getMessageAmount() {
        return messageAmount;
    }

    public void incrementMessageAmount(){
        messageAmount++;
    }

    public int getMediaAmount() {
        return mediaAmount;
    }

    public void incrementMediaAmount(){
        mediaAmount++;
    }

    public Map<String, Integer> getVocabulary() {
        return vocabulary;
    }

    public Map<String, Integer> getEmoticons() {
        return emoticons;
    }

    /**
     * Sort all results according to natural ordering.
     */
    public void sort(){
        vocabulary = ImmutableSortedMap.copyOf(vocabulary,
                Ordering.natural().onResultOf(Functions.forMap(vocabulary)));

        emoticons = ImmutableSortedMap.copyOf(emoticons,
                Ordering.natural().onResultOf(Functions.forMap(emoticons)));
    }

    /**
     * Trim the statistics to reduce storage size and bandwidth consumption.
     * @param length
     */
    public void trim(int length){
        throw new UnsupportedOperationException("Not implemented");
    }
}
