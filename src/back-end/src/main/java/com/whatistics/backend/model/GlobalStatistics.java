package com.whatistics.backend.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Transient;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Virtual class. This class is only used to transfer all Statistics objects relevant for a conversation to the client. It can entirely be reconstructed with the information contained in the Conversation object graph.
 *  Because the database is the interface to the client it has to be precomputed and stored in the database. The precomputation also increases the query performance which is desirable.
 * @author robert
 */
@Entity("globalstatistics")
public class GlobalStatistics {

    @Id
    private ObjectId id;

    @Reference
    private Conversation conversation;

    // Proxy to Conversation.participants for ease of use in client (all messages are embedded in conversations. Would be an overkill to get the whole object for the participants)
    @Reference
    private Set<Person> participants;

    private LinkedHashMap<Date, Integer> aggregatedHistory = new LinkedHashMap<>();

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

    public Map<Date, Integer> getAggregatedHistory() {
        return aggregatedHistory;
    }

    /**
     * Fills gaps in the aggregated history map with 0.
     * For example if there are 3 messages on 15.02.XXXX and 2 messages on 18.02.XXXX it will create two entries on the 16.02 and 17.02 with 0 messages.
     * TODO: This could also done in the initial traversal of the messages array.
     */
    public void inflateAggregatedHistory(){
        LinkedHashMap<Date, Integer> inflated = new LinkedHashMap<>();

        LocalDateTime previousDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(this.aggregatedHistory.entrySet().iterator().next().getKey().getTime()), ZoneOffset.UTC);

        for(Map.Entry<Date, Integer> entry : this.aggregatedHistory.entrySet()){
            LocalDateTime currentDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.getKey().getTime()), ZoneOffset.UTC);

            if(previousDateTime.getYear() != currentDateTime.getYear()){
                previousDateTime = currentDateTime;
                inflated.put(Date.from(currentDateTime.toInstant(ZoneOffset.UTC)), entry.getValue());
                continue;
            }

            while ((currentDateTime.getDayOfYear() - previousDateTime.getDayOfYear()) > 1){
                inflated.put(Date.from(previousDateTime.plusDays(1).toInstant(ZoneOffset.UTC)), 0);
                System.out.println(previousDateTime.plusDays(1));
                previousDateTime = previousDateTime.plusDays(1);
            }
            previousDateTime = currentDateTime;
            inflated.put(Date.from(currentDateTime.toInstant(ZoneOffset.UTC)), entry.getValue());
            System.out.println(currentDateTime);

        }
        this.aggregatedHistory = inflated;
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

    /**
     * Saves the whole object graph.
     * TODO: make a generic version for non circular graphs
     * @param ds
     */
    public void saveObjectGraph(Datastore ds){
        for (Person e : conversation.getParticipants()){
            ds.save(e.getStatistics());
            ds.save(e);
        }

        ds.save(conversation);
        ds.save(statistics);
        ds.save(this);
    }

}
