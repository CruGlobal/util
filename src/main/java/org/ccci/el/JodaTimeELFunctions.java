package org.ccci.el;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.ccci.util.Classes;
import org.ccci.util.time.TimeUtil;
import org.joda.time.Duration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Preconditions;

public class JodaTimeELFunctions
{

    /**
     * 
     * @param instant may be null
     * @param formatter
     *            may be either a {@link DateTimeFormatter} or a string that can be used by
     *            {@link DateTimeFormat#forPattern(String)} to construct a formatter
     * @return null if instant is null; the formatted date otherwise
     */
    public static String formatDate(ReadableInstant instant, Object formatter)
    {
        Preconditions.checkArgument(formatter != null, "formatter is null");
        Preconditions.checkArgument(Classes.isInstanceOf(formatter, String.class, DateTimeFormatter.class),
            "%s is not a String or a DateTimeFormatter");
        DateTimeFormatter actualFormatter;
        if (formatter instanceof String)
        {
            actualFormatter = DateTimeFormat.forPattern((String)formatter);
        }
        else
        {
            actualFormatter = (DateTimeFormatter) formatter;
        }
        return actualFormatter.print(instant);
    }
    
    public static String formatPartial(ReadablePartial partial, Object formatter)
    {
        Preconditions.checkArgument(formatter != null, "formatter is null");
        Preconditions.checkArgument(Classes.isInstanceOf(formatter, String.class, DateTimeFormatter.class),
        "%s is not a String or a DateTimeFormatter");
        DateTimeFormatter actualFormatter;
        if (formatter instanceof String)
        {
            actualFormatter = DateTimeFormat.forPattern((String)formatter);
        }
        else
        {
            actualFormatter = (DateTimeFormatter) formatter;
        }
        return actualFormatter.print(partial);
    }
    
    public static String formatDuration(Duration duration, Object formatter)
    {
        Preconditions.checkArgument(formatter != null, "formatter is null");
        Preconditions.checkArgument(Classes.isInstanceOf(formatter, String.class, NumberFormat.class),
            "%s is not a String or a NumberFormat");
        NumberFormat actualFormatter;
        if (formatter instanceof String)
        {
            actualFormatter = new DecimalFormat((String) formatter);
        }
        else
        {
            actualFormatter = (NumberFormat) formatter;
        }
        return actualFormatter.format(TimeUtil.durationToHours(duration));
    }
}
