package org.ccci.util.mail;

import org.junit.Test;

public class EmailAddressTest
{

    @Test(expected = IllegalArgumentException.class)
    public void testValidation_noExclamationMarks()
    {
        EmailAddress.valueOf("matt.drees!@ccci.org");
    }
}
