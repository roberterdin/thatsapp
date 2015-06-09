package com.whatistics.backend.model;

import com.thedeanda.lorem.Lorem;
import org.mongodb.morphia.annotations.Embedded;

import java.util.Date;

/**
 * Created by robert on 18/05/15.
 */
@Embedded
public class Message {

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

    public Date getSendDate() {
        return sendDate;
    }
    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Message fillWithRandom(){
        this.sendDate = new Date();
        this.content = Lorem.getWords(5, 10);
        this.sender = new Person(Lorem.getFirstName());

        return this;

    }
}
