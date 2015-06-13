package com.whatistics.backend.parser;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author robert
 */
public class TimeFormat implements Comparable {

    private String rawFormat;
    private Locale locale;
    private DateTimeFormatter formatter;

    public TimeFormat(String rawFormat){
        this.rawFormat = rawFormat;
    }

    /**
     * Locale is ignored if the DateTimeFormatter is set directly later.
     * @param rawFormat
     * @param locale
     */
    public TimeFormat(String rawFormat, Locale locale){
        this.rawFormat = rawFormat;
        this.locale = locale;
    }


    public TimeFormat setFormatter(DateTimeFormatter formatter){
        this.formatter = formatter;
        return this;
    }


    public DateTimeFormatter asDateTimeFormatter(){
        if (formatter == null && locale == null)
            formatter = DateTimeFormatter.ofPattern(rawFormat);
        if (formatter == null && locale != null)
            formatter = DateTimeFormatter.ofPattern(rawFormat).withLocale(locale);

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