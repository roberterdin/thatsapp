package com.whatistics.backend.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author robert
 */
@Entity("conversations")
public class Conversation {
    @Id
    private ObjectId id;

    @Embedded
    private List<Message> messages = new ArrayList<>();

    private String submittedBy;

    @Transient
    private javax.mail.Message originalMessage;

    public Conversation() {
    }

    public Conversation(String submittedBy){
        this.submittedBy = submittedBy;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public ObjectId getId() {
        return id;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public javax.mail.Message getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(javax.mail.Message originalMessage) {
        this.originalMessage = originalMessage;
    }
}
