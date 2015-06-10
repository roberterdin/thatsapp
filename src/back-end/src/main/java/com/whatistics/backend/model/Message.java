package com.whatistics.backend.model;

import com.thedeanda.lorem.Lorem;
import org.mongodb.morphia.annotations.Embedded;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author robert
 */
@Embedded
public class Message {

    @Embedded
    private Person sender;

    private String content;

    private LocalDateTime sendDate;

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
        return sendDate;
    }
    public void setSendDate(LocalDateTime sendDate) {
        this.sendDate = sendDate;
    }

    public Message fillWithRandom(){
        this.sendDate = LocalDateTime.now();
        this.content = Lorem.getWords(5, 10);
        this.sender = new Person(Lorem.getFirstName());

        return this;

    }
}
