package org.ccci.faces.convert;


import junit.framework.Assert;

import org.ccci.faces.convert.PartialConverter;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Partial;
import org.testng.annotations.Test;

public class PartialConverterTest
{

    @Test
    public void testGetAsString()
    {
        Partial partial = new Partial().with(DateTimeFieldType.hourOfDay(), 3)
            .with(DateTimeFieldType.minuteOfHour(), 14)
            .with(DateTimeFieldType.secondOfMinute(), 15);
        
        String expected = "[hourOfDay=3, minuteOfHour=14, secondOfMinute=15]";
        String actual = new PartialConverter().getAsString(null, null, partial);
        Assert.assertEquals(expected, actual);
    }
    
    
    @Test
    public void testGetAsString_null()
    {
        Partial partial = null;
        
        String expected = "";
        String actual = new PartialConverter().getAsString(null, null, partial);
        Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void testGetAsObject()
    {
        String string = "[hourOfDay=3, minuteOfHour=14, secondOfMinute=15]";
        
        Partial expected = new Partial().with(DateTimeFieldType.hourOfDay(), 3)
        .with(DateTimeFieldType.minuteOfHour(), 14)
        .with(DateTimeFieldType.secondOfMinute(), 15);
        
        Partial actual = (Partial) new PartialConverter().getAsObject(null, null, string);
        Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void testGetAsObject_null()
    {
        String string = "";
        Partial expected = null;
        Partial actual = (Partial) new PartialConverter().getAsObject(null, null, string);
        Assert.assertEquals(expected, actual);
    }
}
