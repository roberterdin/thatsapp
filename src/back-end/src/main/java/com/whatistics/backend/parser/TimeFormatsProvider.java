package com.whatistics.backend.parser;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author robert
 */
@Singleton
public class TimeFormatsProvider implements Provider<List<TimeFormat>> {

    private List<TimeFormat> timeFormats;

    @Inject
    public TimeFormatsProvider(){
        timeFormats = Arrays.asList(
                // Android en_gb 24h
                // '3 Apr 2011 17:58 - '
                // 14 Dec 2013 18:23 - Nita: Bim ark i de nächi glauv
                new TimeFormat("d MMM yyyy HH:mm - "),

                // Android en_gb 24h
                // '2 Jan 16:10 - '
                new TimeFormat("d MMM HH:mm - "),

                // Android en_us 24h
                // 'Jan 10, 18:14 - '
                new TimeFormat("MMM d, HH:mm - "),

                //  Android en_us 24h  3/6/14, 09:48 -
                // Android en_us 24h   4/20/15, 19:50 -
                new TimeFormat("M/d/yy, HH:mm - "),

                // IOS ??
                // ﻿21.09.14 20:27:11: Moritz: Jou patrick!
                new TimeFormat("dd.MM.yy HH:mm:ss: "),

                // 04.04.16, 19:18 - David Peter:
                new TimeFormat("dd.MM.yy, HH:mm - "),

                // ?? 12h
                // 8:16am, Oct 1 - Nicole Yumi Bae: have a
                new TimeFormat("h:mma, MMM d - "),

                // Android de 24h
                // 4. Mai 2013 00:34 - Sebastian Stephan: David Vögeli ist
                new TimeFormat("d. MMM yyyy HH:mm - ", Locale.GERMAN),

                // Android de 24h
                // 10. Jan. 18:14 - David Schmid: Höt obe in bade?!
               // new TimeFormat("d. MMM. HH:mm - ", Locale.GERMANY),
                new TimeFormat("d. XXX. HH:mm - ").setFormatter(
                        new DateTimeFormatterBuilder()
                                .appendPattern("d. ")
                                .appendText(ChronoField.MONTH_OF_YEAR, ImmutableMap.<Long, String>builder()
                                                .put(1L, "Jan.")
                                                .put(2L, "Feb.")
                                                .put(3L, "Mär.")
                                                .put(4L, "Apr.")
                                                .put(5L, "Mai")
                                                .put(6L, "Juni")
                                                .put(7L, "Juli")
                                                .put(8L, "Aug.")
                                                .put(9L, "Sep.")
                                                .put(10L, "Okt.")
                                                .put(11L, "Nov.")
                                                .put(12L, "Dez.")
                                                .build()
                                )
                                .appendPattern(" HH:mm - ")
                                .parseDefaulting(ChronoField.YEAR, 2016)
                                .toFormatter()
                                .withLocale(Locale.GERMANY)
                )
        );

        Collections.sort(timeFormats);
        Collections.reverse(timeFormats);

        // make readonly for thread safety
        timeFormats = Collections.unmodifiableList(timeFormats);
    }

    @Override
    public List<TimeFormat> get() {
        return timeFormats;
    }
}
