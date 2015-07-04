package com.whatistics.backend.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Transient;

import java.util.*;

/**
 * This class is only used to transfer all Statistics objects relevant for a conversation to the client. It can entirely be reconstructed with the information contained in the Conversation object graph.
 * It should thus be a virtual class. Because the database is the interface to the client it has to be precomputed and stored in the database.
 * @author robert
 */
@Entity("globalstatistics")
public class GlobalStatistics {

    @Id
    private ObjectId id;

    @Reference
    private Conversation conversation;

    // Proxy to Conversation.participants for ease of use in client (messages are embedded in conversations. Would be an overkill to get the whole object for the participants)
    @Reference
    private Set<Person> participants;

    @Reference
    private final Statistics statistics = new Statistics();

    public GlobalStatistics(Conversation conversation){
        this.conversation = conversation;
        this.participants = conversation.getParticipants();
    }

    public ObjectId getId() {
        return id;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    /**
     * Simply calls {@link Statistics#sortAndTrim(int)} on all statistics.
     * Unused because neither MongoDB nor JSON have a contract for ordering elements.
     */
    public void sort(int size){
        this.statistics.sortAndTrim(size);

        conversation.getParticipants().forEach(e -> {
            e.getStatistics().sortAndTrim(size);
        });

    }

    public void saveObjectGraph(Datastore ds){


        for (Person e : conversation.getParticipants()){
            ds.save(e.getStatistics());
            ds.save(e);
        }

        ds.save(conversation);
        ds.save(this);

    }

}
