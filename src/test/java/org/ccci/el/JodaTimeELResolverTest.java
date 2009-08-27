package org.ccci.el;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.MutableDateTime;
import org.joda.time.Partial;
import org.junit.Test;

public class JodaTimeELResolverTest
{

    @Test
    public void testReadMinutesOfHour()
    {
        JodaTimeELResolver resolver = new JodaTimeELResolver();
        MockELContext mockELContext = new MockELContext();
        DateTime dateTime = new DateTime("2008-02-29T12:34:56");
        
        Object value = resolver.getValue(mockELContext , dateTime, "minuteOfHour");
        
        Partial expected = new Partial(DateTimeFieldType.minuteOfHour(), 34);
        Assert.assertEquals(expected, value);
        Assert.assertTrue(mockELContext.isPropertyResolved());
    }
    

    @Test
    public void testWriteMinutesOfHour()
    {
        JodaTimeELResolver resolver = new JodaTimeELResolver();
        MockELContext mockELContext = new MockELContext();
        MutableDateTime mutableDateTime = new MutableDateTime("2008-02-29T12:34:56");
        Partial partial = new Partial(DateTimeFieldType.minuteOfHour(), 17);

        resolver.setValue(mockELContext , mutableDateTime, "minuteOfHour", partial);
        
        MutableDateTime expected = new MutableDateTime("2008-02-29T12:17:56");
        Assert.assertEquals(expected, mutableDateTime);
        Assert.assertTrue(mockELContext.isPropertyResolved());
    }
    
    @Test
    public void testReadHourOfDayRoundFloor()
    {
        JodaTimeELResolver resolver = new JodaTimeELResolver();
        MockELContext mockELContext = new MockELContext();
        DateTime dateTime = new DateTime("2008-02-29T12:34:56");

        Object value = resolver.getValue(mockELContext , dateTime, "hourOfDayRoundFloor");
        
        DateTime expected = new DateTime("2008-02-29T12:00:00");
        Assert.assertEquals(expected, value);
        Assert.assertTrue(mockELContext.isPropertyResolved());
    }
    
    
    
    @Test
    public void testWriteHourOfDayRoundFloor()
    {
        JodaTimeELResolver resolver = new JodaTimeELResolver();
        MockELContext mockELContext = new MockELContext();
        MutableDateTime mutableDateTime = new MutableDateTime("2008-02-29T12:34:56");
        DateTime partialDateTime = new DateTime("2008-02-29T07:00:00");
        
        resolver.setValue(mockELContext, mutableDateTime, "hourOfDayRoundFloor", partialDateTime);

        MutableDateTime expected = new MutableDateTime("2008-02-29T07:34:56");
        Assert.assertEquals(expected, mutableDateTime);
        Assert.assertTrue(mockELContext.isPropertyResolved());
    }
    
}
