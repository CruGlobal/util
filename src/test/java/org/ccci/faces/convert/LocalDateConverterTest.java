package org.ccci.faces.convert;




import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.joda.time.Partial;
import org.testng.annotations.Test;

public class LocalDateConverterTest extends AbstractDateTimeConverterTest
{

    @Test
    public void testGetAsString()
    {
        LocalDate localDate = new LocalDate(2008, 5, 28);
        
        String expected = "2008-05-28";
        String actual = new LocalDateConverter().getAsString(mockFacesContext, null, localDate);
        Assert.assertEquals(expected, actual);
    }
    
    
    @Test
    public void testGetAsString_null()
    {
        LocalDate localDate = null;
        
        String expected = "";
        String actual = new LocalDateConverter().getAsString(mockFacesContext, null, localDate);
        Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void testGetAsObject()
    {
        String string = "2008-05-28";
        
        LocalDate expected = new LocalDate(2008, 5, 28);
        
        LocalDate actual = (LocalDate) new LocalDateConverter().getAsObject(mockFacesContext, null, string);
        Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void testGetAsObject_null()
    {
        String string = "";
        Partial expected = null;
        LocalDate actual = (LocalDate) new LocalDateConverter().getAsObject(mockFacesContext, null, string);
        Assert.assertEquals(expected, actual);
    }
}
