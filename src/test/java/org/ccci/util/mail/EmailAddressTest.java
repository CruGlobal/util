package org.ccci.util.mail;

import org.testng.annotations.Test;

public class EmailAddressTest
{

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testValidation_noExclamationMarks()
    {
        EmailAddress.valueOf("matt.drees!@ccci.org");
    }
}
