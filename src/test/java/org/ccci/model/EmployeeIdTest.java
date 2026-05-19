package org.ccci.model;

import junit.framework.Assert;

import org.testng.annotations.Test;

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

    @Test
    public void testIsValidEmployeeIdWith9Digits()
    {
        Assert.assertTrue(EmployeeId.isValidEmployeeId("123456789"));
        Assert.assertTrue(EmployeeId.isValidEmployeeId("123456789S"));
        Assert.assertTrue(EmployeeId.isValidEmployeeId("123456789D"));
    }

    @Test
    public void testIsValidEmployeeIdWith8Digits()
    {
        Assert.assertTrue(EmployeeId.isValidEmployeeId("12345678"));
    }

    @Test
    public void testIsValidEmployeeIdWithInvalid()
    {
        Assert.assertFalse(EmployeeId.isValidEmployeeId("1234567"));
        Assert.assertFalse(EmployeeId.isValidEmployeeId("1234567890"));
        Assert.assertFalse(EmployeeId.isValidEmployeeId("abcdefghi"));
        Assert.assertFalse(EmployeeId.isValidEmployeeId(""));
        Assert.assertFalse(EmployeeId.isValidEmployeeId(null));
        Assert.assertFalse(EmployeeId.isValidEmployeeId("12345678S"));
        Assert.assertFalse(EmployeeId.isValidEmployeeId("12345678D"));
    }

    @Test
    public void testValueOfWith8Digits()
    {
        EmployeeId id = EmployeeId.valueOf("12345678");
        Assert.assertEquals("12345678", id.getEmployeeId());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testValueOfWith8DigitsAndSuffixThrows()
    {
        EmployeeId.valueOf("12345678S");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testValueOfWith7DigitsThrows()
    {
        EmployeeId.valueOf("1234567");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testValueOfWith10DigitsThrows()
    {
        EmployeeId.valueOf("1234567890");
    }

}
