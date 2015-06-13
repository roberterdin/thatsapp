package com.whatistics.backend.parser;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

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
                new TimeFormat("d MMM yyyy hh:mm - "),

                // Android en_gb 24h
                // '2 Jan 16:10 - '
                new TimeFormat("d MMM hh:mm - "),

                // Android en_us 24h
                // 'Jan 10, 18:14 - '
                new TimeFormat("MMM d, hh:mm - "),

                // IOS ??
                // ﻿21.09.14 20:27:11: Moritz: Jou patrick!
                new TimeFormat("dd.MM.uu HH:mm:ss: "),

                // ?? 12h
                // 8:16am, Oct 1 - Nicole Yumi Bae: have a
                new TimeFormat("h:mma, MMM d - "),

                // Android de 24h
                // 4. Mai 2013 00:34 - Sebastian Stephan: David Vögeli ist
                new TimeFormat("d. MMM yyyy HH:mm - ", Locale.GERMAN),

                // Android de 24h
                // 10. Jan. 18:14 - David Schmid: Höt obe in bade?!
                new TimeFormat("d. MMM. HH:mm - ", Locale.GERMANY)
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
