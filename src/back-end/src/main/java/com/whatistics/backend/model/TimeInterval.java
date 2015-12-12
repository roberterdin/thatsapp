package com.whatistics.backend.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Keep in mind that time intervals are supposed to be half-open!
 * Date is used because MongoDB can´ t deal with Java 8 dates.
 * @author robert
 */
@Entity("timeinverval")
public class TimeInterval{
    @Id
    private ObjectId id;

    private Date startMoment;
    private Date endMoment;
    private String label;
    private Statistics statistics = new Statistics();

    public TimeInterval(){
    }

    public TimeInterval(LocalDateTime startMoment, LocalDateTime endMoment) {
        this.startMoment = Date.from(startMoment.atZone(ZoneId.systemDefault()).toInstant());
        this.endMoment = Date.from(endMoment.atZone(ZoneId.systemDefault()).toInstant());
    }

    public TimeInterval(LocalDateTime startMoment, LocalDateTime endMoment, String label) {
        this(startMoment, endMoment);
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

    public LocalDateTime getStartMoment() {
        return LocalDateTime.ofInstant(this.startMoment.toInstant(), ZoneId.systemDefault());
    }

    public void setStartMoment(Date startMoment) {
        this.startMoment = startMoment;
    }

    public LocalDateTime getEndMoment() {
        return LocalDateTime.ofInstant(this.endMoment.toInstant(), ZoneId.systemDefault());
    }

    public void setEndMoment(Date endMoment) {
        this.endMoment = endMoment;
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
