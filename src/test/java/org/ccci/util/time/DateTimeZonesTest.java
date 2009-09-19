package org.ccci.util.time;

import org.joda.time.DateTimeZone;
import org.testng.Assert;
import org.testng.annotations.Test;

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
    

    @Test
    public void testDateTimeZoneForCountryAndRegionForFlorida()
    {
    	Assert.assertEquals(DateTimeZones.dateTimeZoneForCountryAndRegion("US", "FL"), DateTimeZone.forID("America/New_York"));
    }
    
    @Test(expectedExceptions = RuntimeException.class)
    public void testDateTimeZoneForCountryAndRegionBombsForNullCountry()
    {
    	DateTimeZones.dateTimeZoneForCountryAndRegion(null, null);
    }
    
    
    @Test
    public void testTimeZoneIdForCountryAndRegionForFlorida()
    {
    	Assert.assertEquals(DateTimeZones.timeZoneIdForCountryAndRegion("US", "FL"), "America/New_York");
    }
    
    @Test
    public void testTimeZoneIdForCountryAndRegionForAlberta()
    {
    	Assert.assertEquals(DateTimeZones.timeZoneIdForCountryAndRegion("CA", "AB"), "America/Edmonton");
    }
    
    @Test
    public void testTimeZoneIdForCountryAndRegionForMexico()
    {
    	Assert.assertEquals(DateTimeZones.timeZoneIdForCountryAndRegion("MX", null), "America/Chihuahua");
    }
    
    @Test
    public void testTimeZoneIdForCountryAndRegionForMexicoAndUnknownRegion()
    {
    	Assert.assertEquals(DateTimeZones.timeZoneIdForCountryAndRegion("MX", "SON"), "America/Chihuahua");
    }
    
    @Test
    public void testTimeZoneIdForCountryAndRegionForUnknownCountry()
    {
    	Assert.assertEquals(DateTimeZones.timeZoneIdForCountryAndRegion("ZZ", null), null);
    }
    
    @Test(expectedExceptions = RuntimeException.class)
    public void testTimeZoneIdForCountryAndRegionBombsForNullCountry()
    {
    	DateTimeZones.timeZoneIdForCountryAndRegion(null, null);
    }
    
    @Test(expectedExceptions = RuntimeException.class)
    public void testTimeZoneIdForCountryAndRegionBombsForNullRegionWhenItsRequired()
    {
    	DateTimeZones.timeZoneIdForCountryAndRegion("US", null);
    }
}
