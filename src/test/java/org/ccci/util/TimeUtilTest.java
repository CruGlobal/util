package org.ccci.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.ccci.util.time.TimeUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.annotations.Test;

public class TimeUtilTest
{

    String dstToStandardBoundary = "2008-11-02T01:59:59"; // just before clock is set back
    String standardToDstBoundary = "2008-03-09T01:59:59"; // just before clock is set forward
    String normalDay = "2008-02-17T12:34:56"; // nothing special

    DateTimeZone eastern = DateTimeZone.forID("America/New_York");
    DateTimeZone mountain = DateTimeZone.forID("America/Denver");
    DateTimeZone mountainNoDST = DateTimeZone.forID("America/Phoenix");

    DateTimeZone server = eastern;

    @Test
    public void testDateTimeToSqlTimestamp()
    {

        DateTime normalDayMountain = new DateTime(normalDay, mountain);
        Timestamp timestamp = TimeUtil.dateTimeToSqlTimestamp(normalDayMountain, server);
        String offset = TimeUtil.getZoneOffsetAsString(mountain, normalDayMountain);
        checkTimestamp(timestamp, server, Calendar.FEBRUARY, 17, 12, 34, 56);
        assertEquals("-0700", offset);
    }

    /*
     * Most of America doesn't get 2:05am on the standard-to-dst boundary, but some parts do (AZ and HI, for example).
     * JPA (without an implementation-specific extension) forces us to use the default TimeZone when interacting with
     * JDBC result sets. Currently, our default TimeZone is America/New_York, which goes through DST, so there's no way
     * to pass that time to the database. 
     * It'd be kinda screwy to correct this, so we'll take the ostrich approach.
     * Too bad.
     */
    @Test(expectedExceptions = NotImplementedException.class)
    public void testDateTimeToSqlTimestamp_NoDstOnBorderBombs()
    {
        DateTime uniqueDateTime = new DateTime(standardToDstBoundary, mountainNoDST).plusMinutes(5);
        assert uniqueDateTime.getHourOfDay() == 2;
        TimeUtil.dateTimeToSqlTimestamp(uniqueDateTime, server);
    }

    @Test
    public void testSqlTimestampToDateTime()
    {

        Timestamp timestamp = createTimestamp(server, Calendar.FEBRUARY, 17, 12, 34, 56);
        String offset = "-0700";

        DateTime dateTime = TimeUtil.sqlTimestampToDateTime(timestamp, server, DateTimeZone.forID(offset));
        checkDateTime(dateTime, DateTimeConstants.FEBRUARY, 17, 12, 34, 56);

        timestamp = createTimestamp(server, Calendar.MARCH, 9, 3, 4, 59);
        offset = "-0700";

        dateTime = TimeUtil.sqlTimestampToDateTime(timestamp, server, DateTimeZone.forID(offset));
        checkDateTime(dateTime, DateTimeConstants.MARCH, 9, 3, 4, 59);

    }

    private void checkDateTime(DateTime dateTime, int month, int dayOfMonth, int hourOfDay, int minute, int second)
    {
        assertEquals(month, dateTime.getMonthOfYear());
        assertEquals(dayOfMonth, dateTime.getDayOfMonth());
        assertEquals(hourOfDay, dateTime.getHourOfDay());
        assertEquals(minute, dateTime.getMinuteOfHour());
        assertEquals(second, dateTime.getSecondOfMinute());
    }

    private static Timestamp createTimestamp(DateTimeZone dateTimeZone, int month, int dayOfMonth, int hourOfDay,
                                             int minute, int second)
    {
        GregorianCalendar calendar = new GregorianCalendar(dateTimeZone.toTimeZone());
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return new Timestamp(calendar.getTimeInMillis());
    }

    private static void checkTimestamp(Timestamp timestamp, DateTimeZone dateTimeZone, int month, int dayOfMonth,
                                       int hourOfDay, int minute, int second)
    {
        GregorianCalendar calendar = new GregorianCalendar(dateTimeZone.toTimeZone());
        calendar.setTime(timestamp);
        assertEquals(month, calendar.get(Calendar.MONTH));
        assertEquals(dayOfMonth, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(hourOfDay, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(minute, calendar.get(Calendar.MINUTE));
        assertEquals(second, calendar.get(Calendar.SECOND));
    }
    

    private static void checkTime(Time time, DateTimeZone dateTimeZone, int hourOfDay, int minute, int second)
    {
        GregorianCalendar calendar = new GregorianCalendar(dateTimeZone.toTimeZone());
        calendar.setTime(time);
        assertEquals(hourOfDay, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(minute, calendar.get(Calendar.MINUTE));
        assertEquals(second, calendar.get(Calendar.SECOND));
    }
    
    @Test
    public void testLocalTimeToSqlTime()
    {
        LocalTime localTime = new LocalTime("1:23:45");
        Time time = TimeUtil.localTimeToSqlTime(localTime, server);
        checkTime(time, server, 1, 23, 45);
    }

    
    @Test
    public void testIsDaylightSavingsTimeBoundary()
    {
        LocalDate boundary = new DateTime(dstToStandardBoundary).toLocalDate();
        assertTrue(TimeUtil.isDaylightSavingsTimeBoundary(boundary, eastern));

        LocalDate boundaryNoDst = new DateTime(dstToStandardBoundary).toLocalDate();
        assertFalse(TimeUtil.isDaylightSavingsTimeBoundary(boundaryNoDst, mountainNoDST));
    
        LocalDate nonBoundary = new DateTime(normalDay).toLocalDate();
        assertFalse(TimeUtil.isDaylightSavingsTimeBoundary(nonBoundary, eastern));
    }
    
    @Test
    public void testGetOffsetAsString()
    {
        DateTime dst = new DateTime(dstToStandardBoundary, eastern);
        assertEquals("-0400", TimeUtil.getZoneOffsetAsString(eastern, dst));
        
        DateTime standard = new DateTime(standardToDstBoundary, eastern);
        assertEquals("-0500", TimeUtil.getZoneOffsetAsString(eastern, standard));
        
    }
    
    @Test
    public void testParseLocaldateTime()
    {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
        assertEquals(new LocalDate(2004, 6, 9), TimeUtil.parseLocalDateTime(formatter, "20040609").toLocalDate());
    }

}
