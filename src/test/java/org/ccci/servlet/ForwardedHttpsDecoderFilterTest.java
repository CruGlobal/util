package org.ccci.servlet;

import junit.framework.Assert;

import org.ccci.servlet.ForwardedHttpsDecoderFilter.SslResponse;
import org.testng.annotations.Test;

public class ForwardedHttpsDecoderFilterTest
{

    @Test
    public void testAbsolutePattern()
    {
        Assert.assertTrue(SslResponse.ABSOLUTE_URL_PATTERN.matcher("http://example.org").matches());
    }
}
