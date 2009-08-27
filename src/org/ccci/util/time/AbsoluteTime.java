package org.ccci.util.time;

import org.ccci.util.SimpleValueObject;
import org.ccci.util.strings.ToStringBuilder;
import org.ccci.util.strings.ToStringProperty;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.google.common.base.Preconditions;

/**
 * 
 * Holds a LocalTime and a millisecond offset from UTC.
 * Immutable.  Implements equals() and hashCode()
 * based on contained LocalTime and offset.
 * 
 * @author Matt Drees
 * 
 */
public class AbsoluteTime extends SimpleValueObject
{

    @ToStringProperty
    private final LocalTime localTime;
    
    @ToStringProperty
    private final int offset;

    public AbsoluteTime(LocalTime localTime, int offset)
    {
        Preconditions.checkNotNull(localTime, "localTime is null");
        Preconditions.checkNotNull(offset, "offset is null");
        this.localTime = localTime;
        this.offset = offset;
    }

    public LocalTime getLocalTime()
    {
        return localTime;
    }

    public int getOffset()
    {
        return offset;
    }

    public boolean isBefore(AbsoluteTime other)
    {
        return millisUTC() < other.millisUTC();
    }
    
    public boolean isAfter(AbsoluteTime other)
    {
        return millisUTC() > other.millisUTC();
    }
    
    private long millisUTC()
    {
        return TimeUtil.getLocalMillis(localTime) - offset;
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this).toString();
    }

    public static AbsoluteTime from(LocalTime localTime, LocalDate localDate, DateTimeZone zone)
    {
        return new AbsoluteTime(localTime, zone.getOffset(localDate.toDateTime(localTime, zone)));
    }

    public DateTime toDateTime(LocalDate date)
    {
        return date.toDateTime(localTime, DateTimeZone.forOffsetMillis(offset));
    }

	@Override
	protected Object[] getMembers() {
		return new Object[]{localTime, offset};
	}
    
}
