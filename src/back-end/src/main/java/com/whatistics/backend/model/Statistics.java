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

    /**
     * Increment by the amount provided.
     * @param toAdd The amount of words to be added to the total amount of words.
     */
    public void incrementWordAmount(int toAdd){
        wordAmount = wordAmount + toAdd;
    }

    public int getMessageAmount() {
        return messageAmount;
    }

    /**
     * Increment by 1.
     */
    public void incrementMessageAmount(){
        messageAmount++;
    }

    public int getMediaAmount() {
        return mediaAmount;
    }

    /**
     * Increment by 1.
     */
    public void incrementMediaAmount(){
        mediaAmount++;
    }

    public Map<String, Integer> getVocabulary() {
        return vocabulary;
    }

    /**
     * Increment count of given word by 1.
     * @param word
     */
    public void incrementVocuabulary(String word){
        if(!vocabulary.containsKey(word))
            vocabulary.put(word, 0);

        vocabulary.put(word, vocabulary.get(word) + 1);
    }

    public Map<String, Integer> getEmoticons() {
        return emoticons;
    }

    /**
     * Increment count of given emoji by 1.
     * @param emoji
     */
    public void incrementEmoji(String emoji){
        if(!emoticons.containsKey(emoji))
            emoticons.put(emoji, 0);

        emoticons.put(emoji, emoticons.get(emoji) + 1);
    }

    /**
     * Sort all results according to natural ordering. In the maps, e.g. word-->amount the results are sorted according to the natural ordering of the <b>VALUE</b>.
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
