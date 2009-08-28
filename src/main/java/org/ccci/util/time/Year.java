package org.ccci.util.time;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.ccci.util.ConstructsFromString;
import org.ccci.util.Exceptions;
import org.ccci.util.ValueObject;
import org.joda.time.LocalDate;
import org.joda.time.ReadableInstant;

import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;

/**
 * A useful wrapper for an int representing a year. <br/>
 * Not {@link Embeddable} in JPA entities.
 * 
 * Sidenote: see http://www.koders.com/java/fid707B356089679C67AE26208466B09F83659D4BE2.aspx
 * 
 * @author Matt Drees
 *
 */
public class Year extends ValueObject implements Serializable, Comparable<Year>
{
    private static final long serialVersionUID = 1L;

    private final int year;

    protected Year(int year)
    {
        this.year = year;
    }

    /**
     * Returns the common integer representation of this year, e.g. 1993
     */
    //TODO: potentially rename to intValue()
    public int getYear()
    {
        return year;
    }

    public LocalDate getStartDate()
    {
        return new LocalDate(year, 1, 1);
    }

    public Year nextYear()
    {
        return newYear(year + 1);
    }
    
    public Year previousYear()
    {
    	return newYear(year - 1);
    }

    public boolean isBefore(Year year)
    {
        return compareTo(year) < 0;
    }
    
    @Override
    public int compareTo(Year other)
    {
        Preconditions.checkNotNull(other, "other is null");
        return Ordering.natural().compare(this.year, other.year);
    }

    public boolean isAfter(Year year)
    {
        return compareTo(year) > 0;
    }
    
    @Override
    public String toString()
    {
        return String.valueOf(year);
    }
    
    public static Year newYear(int year)
    {
        return new Year(year);
    }
    
    /**
     * Create a Year that contains the given {@link LocalDate}
     * @param localDate
     * @return
     */
    public static Year newYear(LocalDate localDate)
    {
        Preconditions.checkNotNull(localDate, "localDate is null");
        return newYear(localDate.toDateTimeAtStartOfDay());
    }

    /**
     * Create a Year that contains the given {@link ReadableInstant}
     * @param localDate
     * @return
     */
    public static Year newYear(ReadableInstant instant)
    {
        Preconditions.checkNotNull(instant, "instant is null");
        return new Year(instant.toInstant().toDateTime().getYear());
    }
    
    @ConstructsFromString
    public static Year valueOf(String year)
    {
        Preconditions.checkNotNull(year, "year is null");
        try
        {
            return new Year(Integer.parseInt(year));
        }
        catch (NumberFormatException e)
        {
            throw Exceptions.newIllegalArgumentException(e, "not a valid year: %s", year);
        }
    }

	@Override
	protected Object[] getComponents() {
		return new Object[]{year};
	}
    
}
