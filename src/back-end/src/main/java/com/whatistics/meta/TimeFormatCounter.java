package com.whatistics.meta;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Class to help keeping track of which {@link com.whatistics.backend.parser.TimeFormat} implementations from {@link com.whatistics.backend.parser.TimeFormatsProvider} are actually used. If there is enough data this might help improve performance by checking for common ones first.
 * There are no members because the increase method of MongoDB will create a member if it does not exist.
 */
@Entity
public class TimeFormatCounter {
    @Id
    private Integer id;
}
