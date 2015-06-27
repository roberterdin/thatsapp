package com.whatistics.backend.model;

import com.google.common.collect.ImmutableMap;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
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
     *
     * @param size the size to which the maps are reduced
     */
    public void sortAndTrim(int size){
        ImmutableMap.Builder<String, Integer> builder = ImmutableMap.builder();

        vocabulary.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(size)
                .forEachOrdered(builder::put);

        vocabulary = builder.build();

        builder = ImmutableMap.builder();

        emoticons.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(size)
                .forEachOrdered(builder::put);

        emoticons = builder.build();
    }
}
