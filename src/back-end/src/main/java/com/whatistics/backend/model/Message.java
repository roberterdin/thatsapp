package com.whatistics.backend.model;

import com.thedeanda.lorem.Lorem;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Reference;

import java.time.*;
import java.util.Date;

/**
 * @author robert
 */
@Embedded
public class Message {

    // not used for persistence! object is embedded in mongodb. Used for Ember
    private ObjectId _id = ObjectId.get();

    @Reference
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
        ZonedDateTime zdt = sendDate.atZone(ZoneId.of("Z")); // UTC
        this.sendDate = Date.from(zdt.toInstant());
    }

    public void setSendDate(Date date){
        this.sendDate = new Date(date.getTime());
    }

    public Message fillWithRandom(){
        this.sendDate = new Date();
        this.content = Lorem.getWords(5, 10);
        this.sender = new Person(Lorem.getFirstName());

        return this;
    }
}
