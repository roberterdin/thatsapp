package com.whatistics.backend.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robert on 18/05/15.
 */
@Entity("conversations")
public class Conversation {
    @Id
    private ObjectId id;

    @Embedded
    private List<Message> messages = new ArrayList<>();

    public Conversation() {
    }

    public List<Message> getMessages() {
        return messages;
    }

    public ObjectId getId() {
        return id;
    }
}
