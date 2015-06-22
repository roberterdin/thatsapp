package com.whatistics.backend.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.util.UUID;

/**
 * @author robert
 */
@Embedded
public class Person {

    // not used for persistence! object is embedded in mongodb. Used for Ember
    private ObjectId _id = ObjectId.get();

    private String name;

    public Person(){

    }
    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
