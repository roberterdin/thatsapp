package com.whatistics.backend.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author robert
 */
@Entity("persons")
public class Person {

    @Id
    private ObjectId id;

    private String name;

    private Statistics statistics;

    public Person(){
        this.statistics = new Statistics();
    }
    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Statistics getStatistics() {
        return statistics;
    }


}
