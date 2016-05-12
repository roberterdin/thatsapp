package com.whatistics.backend.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Virtual class. This class is only used to transfer all Statistics objects relevant for a conversation to the client. It can entirely be reconstructed with the information contained in the Conversation object graph.
 *  Because the database is the interface to the client it has to be precomputed and stored in the database. The precomputation also increases the query performance which is desirable.
 * @author robert
 */
@Entity("globalstatistics")
public class GlobalStatistics {

    private final Logger logger = LoggerFactory.getLogger(GlobalStatistics.class);

    @Id
    private ObjectId id;

    @Reference
    private Conversation conversation;

    public Set<Person> getParticipants() {
        return participants;
    }

    // Proxy to Conversation.participants for ease of use in client (all messages are embedded in conversations. Would be an overkill to get the whole object for the participants)
    @Reference
    private Set<Person> participants;

    // List instead of a LinkedHashMap to preserve order in the database and serialization.
    private List<TimeInterval> aggregatedHistory = new LinkedList<>();


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

    public List<TimeInterval> getAggregatedHistory() {
        return aggregatedHistory;
    }

    /**
     * Fills gaps in the aggregated history map with 0.
     * For example if there are 3 messages on 15.02.XXXX and 2 messages on 18.02.XXXX it will create two entries on the 16.02 and 17.02 with 0 messages.
     * TODO: This could also done in the initial traversal of the messages array.
     */
    public void inflateAggregatedHistory(){
        LinkedHashMap<Date, Integer> inflated = new LinkedHashMap<>();

        for(ListIterator<TimeInterval> iter = this.aggregatedHistory.listIterator(); iter.hasNext();){
            TimeInterval current = iter.next();
            if(iter.hasNext()){
                TimeInterval next = iter.next();
                iter.previous(); // reset

                // TODO handle new year better
                if(next.getStartInstant().getYear() != current.getStartInstant().getYear()){
                    continue;
                }

                while ((next.getStartInstant().getDayOfYear() - current.getStartInstant().getDayOfYear()) > 1){
                    // insert
                    TimeInterval toInsert = new TimeInterval(next.getStartInstant().minusDays(1),
                            next.getEndInstant().minusDays(1));
                    iter.add(toInsert);
                    next = toInsert;
                }
            }
        }
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
