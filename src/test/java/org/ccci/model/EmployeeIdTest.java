package org.ccci.model;

import junit.framework.Assert;

import org.junit.Test;

public class EmployeeIdTest
{

    @Test
    public void testCoerceWithValid()
    {
        checkCoerceValid("123456789", "123456789");
        checkCoerceValid("123456789S", "123456789S");
        checkCoerceValid("123456789S", "123456789s");
        checkCoerceValid("123456789S", " 123456789s");
        checkCoerceValid("000000042", " 42");
        checkCoerceValid("000000042S", " 42S");
    }

    private void checkCoerceValid(String expectedEmployeeId, String string)
    {
        Assert.assertEquals(EmployeeId.valueOf(expectedEmployeeId), EmployeeId.coerce(string));
    }

    @Test
    public void testCoerceWithInvalid()
    {
        checkCoerceInvalid("abcdf");
        checkCoerceInvalid("");
        checkCoerceInvalid("123456789 123456789");
        checkCoerceInvalid("42 42");
    }
    

    private void checkCoerceInvalid(String string)
    {
        Assert.assertNull(EmployeeId.coerce(string));
    }

}
