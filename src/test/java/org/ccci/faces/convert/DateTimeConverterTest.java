package org.ccci.faces.convert;


import java.util.Locale;

import javax.faces.component.UIViewRoot;

import junit.framework.Assert;

import org.jboss.seam.mock.MockApplication;
import org.jboss.seam.mock.MockFacesContext;
import org.joda.time.LocalDate;
import org.joda.time.Partial;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DateTimeConverterTest
{

    MockFacesContext mockFacesContext;
    
    @Before
    public void setUpFacesContext()
    {
        
        mockFacesContext = new MockFacesContext(null, new MockApplication());
        mockFacesContext.setCurrent();
        mockFacesContext.setViewRoot(new UIViewRoot() {
            
            /**
             * to avoid having to mock a ViewHandler.  See {@link UIViewRoot#getLocale()}
             */ 
            @Override
            public Locale getLocale()
            {
                return null; 
            }
        });
    }
    
    @After
    public void cleanup()
    {
        mockFacesContext.release();
    }
    
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
