package org.ccci.util.mail;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.Embeddable;
import javax.persistence.PostLoad;

import org.ccci.dao.IllegalDatabaseStateException;
import org.ccci.util.ConstructsFromString;
import org.ccci.util.Exceptions;
import org.ccci.util.SimpleValueObject;

import com.google.common.base.Preconditions;

/**
 * Value object representing a valid email address.
 * 
 * Maybe @Embedded into a jpa entity.
 * 
 * @author Matt Drees
 */
@Embeddable
public class EmailAddress extends SimpleValueObject implements Serializable, InternetAddressable
{
	private static final long serialVersionUID = 1L;

	private String emailAddress;

    //for JPA
    protected EmailAddress()
    {
    }
    
    protected EmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    @PostLoad
    protected void validate()
    {
        Preconditions.checkNotNull(emailAddress, "emailAddress in database is null");
        try
        {
            new InternetAddress(emailAddress, false).validate();
        }
        catch (AddressException e)
        {
            throw new IllegalDatabaseStateException(String.format("invalid email address in database: %s", emailAddress), e);
        }
        checkUsable(emailAddress);
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }
    
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
            new InternetAddress(emailAddress, false).validate();
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
            throw new AssertionError("validation should have occured during construction, yet caught: " + e);
        }
    }

	@Override
	protected Object[] getMembers() {
		return new Object[]{emailAddress};
	}
    
}
