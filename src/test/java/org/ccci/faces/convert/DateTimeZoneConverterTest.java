package org.ccci.faces.convert;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Test;

public class DateTimeZoneConverterTest extends AbstractDateTimeConverterTest
{

    private static final DateTime aDate = new DateTime(2008, 5, 30, 3, 14, 15, 0);

    @Test
    public void testGetAsObject()
    {
        DateTime expected = aDate;
        String value = "May 30, 2008 3:14:15 AM";

        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        Assert.assertEquals(expected, dateTimeConverter.getAsObject(mockFacesContext, dummyComponent(DateTime.class),
            value));
    }

    @Test
    public void testGetAsObject_null()
    {
        DateTime expected = null;
        String value = "";
        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        Assert.assertEquals(expected, dateTimeConverter.getAsObject(mockFacesContext, dummyComponent(DateTime.class),
            value));
    }

    @Test
    public void testGetAsString()
    {
        String expected = "May 30, 2008 3:14:15 AM";
        DateTime dateTime = aDate;
        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        Assert.assertEquals(expected, dateTimeConverter.getAsString(mockFacesContext, dummyComponent(DateTime.class),
            dateTime));
    }

    @Test
    public void testGetAsString_null()
    {
        String expected = "";
        DateTime dateTime = null;
        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        Assert.assertEquals(expected, dateTimeConverter.getAsString(mockFacesContext, dummyComponent(DateTime.class),
            dateTime));
    }

}
