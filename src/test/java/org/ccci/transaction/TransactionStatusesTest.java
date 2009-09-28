package org.ccci.transaction;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TransactionStatusesTest
{

    @Test
    public void testToString()
    {
        Assert.assertEquals(TransactionStatuses.toString(0), "ACTIVE");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testToStringBombsWithInvalidInput()
    {
        TransactionStatuses.toString(11);
    }

}
