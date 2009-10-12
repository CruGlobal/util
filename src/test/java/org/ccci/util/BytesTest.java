package org.ccci.util;

import java.nio.charset.Charset;

import org.testng.Assert;
import org.testng.annotations.Test;

public class BytesTest
{

    @Test
    public void testByteArrayToHexString()
    {
        byte[] bytes = new byte[]{0x03, 0x14, 0x2a, 0x0d };
        String expected = "03142a0d";
        Assert.assertEquals(expected, Bytes.toHexString(bytes));
    }
    
    @Test
    public void testByteArrayToBase64String()
    {
        //test data from http://en.wikipedia.org/wiki/Base64#Example
        byte[] bytes = "Man".getBytes(Charset.forName("US-ASCII"));
        String expected = "TWFu";
        Assert.assertEquals(expected, Bytes.toBase64String(bytes));
    }
    
}
