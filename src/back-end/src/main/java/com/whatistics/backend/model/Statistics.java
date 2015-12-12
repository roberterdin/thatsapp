package com.whatistics.backend.model;

import com.google.common.collect.ImmutableMap;
import com.whatistics.backend.WhatisticsBackend;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.*;

/**
 * @author robert
 */
@Entity("statistics")
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

    public void setMessageAmount(int messageAmount) {
        this.messageAmount = messageAmount;
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

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    /**
     * Sort all results according to natural ordering. In the maps, e.g. word-->amount the results are sorted according to the natural ordering of the <b>VALUE</b>.
     *
     * @param size the size to which the maps are reduced
     */
    public void sortAndTrim(int size){

        vocabulary = sortByValueAndTrim(vocabulary, size);
        emoticons = sortByValueAndTrim(emoticons, size);
    }

    /**
     * <b>Unused because MongoDB does not preserve order! This is ok because neither does JSON make this guarantee</b>
     * Sorts a map in descending order and trims it.
     * @param map The map to be sorted and trimmed
     * @param size The size the given map is to be trimmed down
     * @param <K> The type of the key
     * @param <V> The type of the value
     * @return Returns a {@link ImmutableMap}
     */
    private static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValueAndTrim(Map<K, V> map, int size)
    {
        ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();

        map.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(size)
                .forEachOrdered(builder::put);

        Map<K, V> result = builder.build();

        return result;
    }

    /**
     * Unused, redundant version to {@link Statistics#sortByValueAndTrim(Map, int)}. Does not rely on Guava.
     * TODO: Test if faster than Guava version
     * @param map Map to be sorted
     * @param <K> Type of Key
     * @param <V> Type of Value
     * @return Returns a {@link LinkedHashMap}
     */
    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map, int size )
    {
        Map<K,V> result = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Collections.reverseOrder(Comparator.comparing(e -> e.getValue())))
                .limit(size)
                .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }

    public void fillWithRandom(){
        this.mediaAmount = WhatisticsBackend.rand.nextInt((100 - 1) + 1) + 1;
        this.messageAmount = WhatisticsBackend.rand.nextInt((100 - 1) + 1) + 1;
        this.wordAmount = WhatisticsBackend.rand.nextInt((100 - 1) + 1) + 1;
    }
}
