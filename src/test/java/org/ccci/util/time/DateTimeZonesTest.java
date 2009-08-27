package org.ccci.util.time;

import org.joda.time.DateTimeZone;
import org.junit.Test;

public class DateTimeZonesTest
{

    @Test
    public void testAmericanZones()
    {
        for (DateTimeZone zone : DateTimeZones.americanZones())
        {
            printZone(zone);
        }
    }

    private void printZone(DateTimeZone zone)
    {
//        long instant = new LocalDate(2008, 1, 2).toDateTimeAtCurrentTime().getMillis();
//        System.out.println(zone.getID() + ": " + zone.getName(instant));
    }
}
