package com.whatistics.backend.model;


import org.mongodb.morphia.annotations.Entity;

import java.util.HashMap;
import java.util.Map;

@Entity
public class TestEntity {

    private final Map<String, Integer> map = new HashMap<>();

    public TestEntity() {
    }

    public Map<String, Integer> getMap() {
        return map;
    }

}
