package org.ccci.util;

import org.ccci.util.strings.Strings;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StringsTest
{

    @Test(expectedExceptions = NullPointerException.class)
    public void testRepeatWithNullInput()
    {
        Strings.repeat(null, 3);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRepeatWithBadRepeat()
    {
        Strings.repeat("foo", -1);
    }
    
    @Test
    public void testRepeatWithZeroRepeat()
    {
        Assert.assertEquals(Strings.repeat("foo", 0), "");
    }
    
    @Test
    public void testRepeatWithOneRepeat()
    {
        Assert.assertEquals(Strings.repeat("foo", 1), "foo");
    }
    
    @Test
    public void testRepeatWithTwoRepeats()
    {
        Assert.assertEquals(Strings.repeat("foo", 1), "foo");
    }
    
    
    
}
