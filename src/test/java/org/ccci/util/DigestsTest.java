package org.ccci.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DigestsTest
{

    @Test
    public void testSha1Digest()
    {
        String message = "The quick brown fox jumps over the lazy dog";
        String digested = Digests.sha1Digest(message);
        Assert.assertEquals(digested, "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12");
    }
}
