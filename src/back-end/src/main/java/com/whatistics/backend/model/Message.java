package com.whatistics.backend.model;

import com.thedeanda.lorem.Lorem;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Id;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

/**
 * @author robert
 */
@Embedded
public class Message {

    // not used for persistence! object is embedded in mongodb. Used for Ember
    private ObjectId id = ObjectId.get();

    @Embedded
    private Person sender;

    private String content;

    private Date sendDate;

    public Message() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Person getSender() {
        return sender;
    }

    public void setSender(Person sender) {
        this.sender = sender;
    }

    public LocalDateTime getSendDate() {
        Instant instant = Instant.ofEpochMilli(sendDate.getTime());
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public void setSendDate(LocalDateTime sendDate) {
        Instant instant = sendDate.toInstant(ZoneOffset.UTC);
        this.sendDate = Date.from(instant);
    }


    public Message fillWithRandom(){
        this.sendDate = new Date();
        this.content = Lorem.getWords(5, 10);
        this.sender = new Person(Lorem.getFirstName());

        return this;
    }
}
