package com.whatistics.backend.parser;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author robert
 * Note: this class has a natural ordering that is inconsistent with equals.
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

    public String getRawFormat() {
        return rawFormat;
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


    @Override
    public int hashCode() {
        int result = rawFormat != null ? rawFormat.hashCode() : 0;
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        result = 31 * result + (formatter != null ? formatter.hashCode() : 0);
        return result;
    }


}