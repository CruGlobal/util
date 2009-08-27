package org.ccci.util.time;

import static org.joda.time.DateTimeFieldType.centuryOfEra;
import static org.joda.time.DateTimeFieldType.clockhourOfDay;
import static org.joda.time.DateTimeFieldType.clockhourOfHalfday;
import static org.joda.time.DateTimeFieldType.dayOfMonth;
import static org.joda.time.DateTimeFieldType.dayOfWeek;
import static org.joda.time.DateTimeFieldType.dayOfYear;
import static org.joda.time.DateTimeFieldType.era;
import static org.joda.time.DateTimeFieldType.halfdayOfDay;
import static org.joda.time.DateTimeFieldType.hourOfDay;
import static org.joda.time.DateTimeFieldType.hourOfHalfday;
import static org.joda.time.DateTimeFieldType.millisOfDay;
import static org.joda.time.DateTimeFieldType.millisOfSecond;
import static org.joda.time.DateTimeFieldType.minuteOfDay;
import static org.joda.time.DateTimeFieldType.minuteOfHour;
import static org.joda.time.DateTimeFieldType.monthOfYear;
import static org.joda.time.DateTimeFieldType.secondOfDay;
import static org.joda.time.DateTimeFieldType.secondOfMinute;
import static org.joda.time.DateTimeFieldType.weekOfWeekyear;
import static org.joda.time.DateTimeFieldType.weekyear;
import static org.joda.time.DateTimeFieldType.weekyearOfCentury;
import static org.joda.time.DateTimeFieldType.year;
import static org.joda.time.DateTimeFieldType.yearOfCentury;
import static org.joda.time.DateTimeFieldType.yearOfEra;

import java.util.Map;

import org.joda.time.DateTimeFieldType;

import com.google.common.collect.ImmutableMap;

public class JodaHelper
{

    /**
     * how I wish there was a DateTimeFieldType.forName(String) method... 
     * 
     * maybe should be renamed.  Not sure how I feel about static final map names.
     */
    public static final Map<String, DateTimeFieldType> DATE_TIME_FIELD_TYPE_NAME_TO_DATE_TIME_FIELD 
        = ImmutableMap.<String, DateTimeFieldType>builder()
            .put(era().getName(), era())
            .put(yearOfEra().getName(), yearOfEra())
            .put(centuryOfEra().getName(), centuryOfEra())
            .put(yearOfCentury().getName(), yearOfCentury())
            .put(year().getName(), year())
            .put(dayOfYear().getName(), dayOfYear())
            .put(monthOfYear().getName(), monthOfYear())
            .put(dayOfMonth().getName(), dayOfMonth())
            .put(weekyearOfCentury().getName(), weekyearOfCentury())
            .put(weekyear().getName(), weekyear())
            .put(weekOfWeekyear().getName(), weekOfWeekyear())
            .put(dayOfWeek().getName(), dayOfWeek())
            .put(halfdayOfDay().getName(), halfdayOfDay())
            .put(hourOfHalfday().getName(), hourOfHalfday())
            .put(clockhourOfHalfday().getName(), clockhourOfHalfday())
            .put(clockhourOfDay().getName(), clockhourOfDay())
            .put(hourOfDay().getName(), hourOfDay())
            .put(minuteOfDay().getName(), minuteOfDay())
            .put(minuteOfHour().getName(), minuteOfHour())
            .put(secondOfDay().getName(), secondOfDay())
            .put(secondOfMinute().getName(), secondOfMinute())
            .put(millisOfDay().getName(), millisOfDay())
            .put(millisOfSecond().getName(), millisOfSecond())
            .build();

}
