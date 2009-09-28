package org.ccci.util;

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
}
