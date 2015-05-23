package com.whatistics.backend.model;

import org.mongodb.morphia.annotations.Embedded;

/**
 * Created by robert on 18/05/15.
 */
@Embedded
public class Person {
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
