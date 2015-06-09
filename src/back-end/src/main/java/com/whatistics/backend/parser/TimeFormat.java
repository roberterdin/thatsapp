package com.whatistics.backend.parser;

import java.sql.Time;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

/**
 * @author robert
 */
public class TimeFormat implements Comparable {

    private String rawFormat;
    private DateTimeFormatter formatter;

    public TimeFormat(String rawFormat){
        this.rawFormat = rawFormat;
    }

    public DateTimeFormatter asDateTimeFormatter(){
        if (formatter == null)
            formatter = DateTimeFormatter.ofPattern(rawFormat);
        return formatter;
    }

    public int getLength(){
        return rawFormat.length();
    }

    @Override
    public int compareTo(Object o) {
        TimeFormat p = (TimeFormat)o;
        if(p.getLength() < this.getLength())
            return 1;
        if(p.getLength() > this.getLength())
            return -1;
        else
            return 0;
    }
}