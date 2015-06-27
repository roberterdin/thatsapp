package com.whatistics.backend.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is only used to transfer all Statistics objects relevant for a conversation to the client. It can entirely be reconstructed with the information.
 * It should thus be a virtual class. Because the database is the interface to the client it has to be precomputed and stored in the database.
 * @author robert
 */
@Entity("globalstatistics")
public class GlobalStatistics {

    @Id
    private ObjectId id;

    // shortcut for serialisation
    private final Map<Person, Statistics> composingStatistics = new HashMap<>();

    private Conversation conversation;
    private final Statistics statistics = new Statistics();

    public GlobalStatistics(Conversation conversation){
        this.conversation = conversation;
    }

    public ObjectId getId() {
        return id;
    }

    public Map<Person, Statistics> getComposingStatistics() {
        return composingStatistics;
    }

    public Statistics getPersonalStats(Person person){
        if(!composingStatistics.containsKey(person)){
            composingStatistics.put(person, person.getStatistics());
        }
        return composingStatistics.get(person);
    }

    public Statistics getStatistics() {
        return statistics;
    }

    /**
     * Simply calls {@link Statistics#sortAndTrim(int)} on all statistics.
     */
    public void sort(int size){
        this.statistics.sortAndTrim(size);
        for (Map.Entry<Person, Statistics> entry : composingStatistics.entrySet()){
            entry.getValue().sortAndTrim(size);
        }
    }
}
