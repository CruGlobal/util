package org.ccci.util.mail;

import javax.mail.internet.InternetAddress;

public interface InternetAddressable
{
    /**
     * Return an email address usable by the java mail api
     */
    InternetAddress toInternetAddress();
}
