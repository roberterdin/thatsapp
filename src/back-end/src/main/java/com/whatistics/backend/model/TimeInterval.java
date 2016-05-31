package com.whatistics.backend.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Keep in mind that time intervals are supposed to be half-open!
 * Date is used because MongoDB can´ t deal with Java 8 dates.
 * @author robert
 */
@Entity("timeinverval")
public class TimeInterval{
    @Id
    private ObjectId id = new ObjectId();

    private Date startInstant;
    private Date endInstant;
    private String label;
    private Statistics statistics = new Statistics();

    /**
     * Creates a TimeInterval with a default label in the dd.MM.yyyy format.
     * @param startInstant
     * @param endInstant
     */
    public TimeInterval(LocalDateTime startInstant, LocalDateTime endInstant) {
        this.startInstant = Date.from(startInstant.atZone(ZoneId.systemDefault()).toInstant());
        this.endInstant = Date.from(endInstant.atZone(ZoneId.systemDefault()).toInstant());
        this.label = startInstant.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    /**
     * Overwrites default label.
     * @param startInstant
     * @param endInstant
     * @param label
     */
    public TimeInterval(LocalDateTime startInstant, LocalDateTime endInstant, String label) {
        this(startInstant, endInstant);
        this.label = label;
    }

    /**
     * Create a TimeInterval which is one calendar day long, given any Date object within this date.
     * E.g. 12.02.2015, 15:33 --> 12.02.2015, 00:00:00 ; 13.02.2015, 00:00:00 since the day is half-open
     * @param moment
     * @return
     */
    public static TimeInterval createDay(LocalDateTime moment){

        LocalDateTime startOfDay = moment.toLocalDate().atStartOfDay();
        LocalDateTime tomorrowStart = startOfDay.plusDays(1);

        return new TimeInterval(startOfDay, tomorrowStart);
    }

    public LocalDateTime getStartInstant() {
        return LocalDateTime.ofInstant(this.startInstant.toInstant(), ZoneId.systemDefault());
    }

    public void setStartInstant(Date startInstant) {
        this.startInstant = new Date(startInstant.getTime());
    }

    public LocalDateTime getEndInstant() {
        return LocalDateTime.ofInstant(this.endInstant.toInstant(), ZoneId.systemDefault());
    }

    public void setEndInstant(Date endInstant) {
        this.endInstant = new Date(endInstant.getTime());
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
}
