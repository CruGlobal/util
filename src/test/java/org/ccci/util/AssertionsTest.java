package org.ccci.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class AssertionsTest
{

    @Test
    public void testError()
    {
        try
        {
            Assertions.error("bad apple: %s", "worm");
            Assert.fail("no error thrown");
        }
        catch (AssertionError error)
        {
            Assert.assertEquals(error.getMessage(), "bad apple: worm");
        }
    }
    
    @Test
    public void testErrorWithCause()
    {
        IllegalArgumentException cause = new IllegalArgumentException();
        try
        {
            Assertions.error(cause, "bad apple: %s", "worm");
            Assert.fail("no error thrown");
        }
        catch (AssertionError error)
        {
            Assert.assertEquals(error.getMessage(), "bad apple: worm");
            Assert.assertEquals(error.getCause(), cause);
        }
    }
}
