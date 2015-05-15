package org.ccci.util.mail;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.ccci.util.Assertions;
import org.ccci.util.ConstructsFromString;
import org.ccci.util.Exceptions;
import org.ccci.util.ValueObject;

import com.google.common.base.Preconditions;

/**
 * Value object representing a valid email address.
 * 
 * 
 * @author Matt Drees
 */
public class EmailAddress extends ValueObject implements Serializable, InternetAddressable
{

	private final String emailAddress;
    
    protected EmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    /**
     * For example, "matt.drees@ccci.org"
     */
    @Override
    public String toString()
    {
        return emailAddress;
    }
    
    /**
     * Validates argument by constructing an {@link InternetAddress} with nonstrict parsing.
     * @param emailAddress
     * @throws IllegalArgumentException if the given string is not a valid email address; the cause will
     * be an {@link AddressException} containing the parse error
     * @return
     */
    @ConstructsFromString
    public static EmailAddress valueOf(String emailAddress) throws IllegalArgumentException
    {
        Preconditions.checkNotNull(emailAddress, "emailAddress is null");
        try
        {
            InternetAddress internetAddress = new InternetAddress();
            internetAddress.setAddress(emailAddress);
            internetAddress.validate();
        }
        catch (AddressException e)
        {
            throw Exceptions.newIllegalArgumentException(e, "invalid email address: %s", emailAddress);
        }
        checkUsable(emailAddress);
        return new EmailAddress(emailAddress);
    }

    private static void checkUsable(String emailAddress)
    {
        if (isEffectivelyUnusableAddress(emailAddress))
        {
            throw new IllegalArgumentException("Address effectively unusable: " + emailAddress);
        }
    }

    /**
     * Some email addresses are valid according to RFC 2822 (in particular, sample!@ccci.org), but when sending to
     * them via the SMTP server, the send bombs with a "badly formatted recipient" message. I suspect this is only
     * for internal email addresses (the smtp server would know our addresses never have a "!" in them), but it's
     * hard to enumerate all internal domains, so we check for strange characters in all domains, not just our
     * own.
     * 
     * @param emailAddress
     */
    private static boolean isEffectivelyUnusableAddress(String emailAddress)
    {
        return emailAddress.contains("!");
    }

    /**
     * Constructs an InternetAddress from this email address and the given {@code personalName}
     * @param personalName
     * @throws IllegalArgumentException if the {@code personalName} contains unsupported characters
     */
    public InternetAddress toInternetAddress(String personalName) throws IllegalArgumentException
    {
        try
        {
            return new InternetAddress(emailAddress, personalName);
        }
        catch (UnsupportedEncodingException e)
        {
            throw Exceptions.newIllegalArgumentException(e, "personalName cannot be encoded: %s", personalName);
        }
    }

    public InternetAddress toInternetAddress()
    {
        try
        {
            return new InternetAddress(emailAddress, false);
        }
        catch (AddressException e)
        {
            throw Assertions.error(e, "validation should have occured during construction");
        }
    }

	@Override
	protected Object[] getComponents() {
		return new Object[]{emailAddress};
	}

    private static final long serialVersionUID = 1L;
}
