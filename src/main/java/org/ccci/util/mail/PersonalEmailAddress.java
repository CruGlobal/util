package org.ccci.util.mail;

import java.io.Serializable;

import javax.mail.internet.InternetAddress;

import org.ccci.util.ValueObject;

import com.google.common.base.Preconditions;

public class PersonalEmailAddress  extends ValueObject implements Serializable, InternetAddressable
{

    private final EmailAddress emailAddress;
    private final String personalName;

    /**
     * Construct a {@link PersonalEmailAddress} with the given emailAddress and personalName.
     * @param emailAddress required
     * @param personalName required
     * @throws IllegalArgumentException if {@code emailAddress} is an invalid email address; 
     *  see {@link EmailAddress#valueOf(String)}
     * @return
     */
    public static PersonalEmailAddress newPersonalEmailAddress(String emailAddress, String personalName)
    {
        Preconditions.checkNotNull(emailAddress, "emailAddress is null");
        Preconditions.checkNotNull(personalName, "personalName is null");
        return new PersonalEmailAddress(EmailAddress.valueOf(emailAddress), personalName);
    }
    
    /**
     * Construct a {@link PersonalEmailAddress} with the given emailAddress and personalName.
     * @param emailAddress required
     * @param personalName required
     * @return
     */    
    public static PersonalEmailAddress newPersonalEmailAddress(EmailAddress emailAddress, String personalName)
    {
        Preconditions.checkNotNull(emailAddress, "emailAddress is null");
        Preconditions.checkNotNull(personalName, "personalName is null");        
        return new PersonalEmailAddress(emailAddress, personalName);
    }
    
    private PersonalEmailAddress(EmailAddress emailAddress, String name)
    {
        this.emailAddress = emailAddress;
        this.personalName = name;
    }
    
    public EmailAddress getEmailAddress()
    {
        return emailAddress;
    }

    public String getPersonalName()
    {
        return personalName;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(personalName)
            .append(" <")
            .append(emailAddress)
            .append(">")
            .toString();
    }
    
    @Override
    protected Object[] getComponents()
    {
        return new Object[]{emailAddress, personalName};
    }
    
    private static final long serialVersionUID = 1L;

    @Override
    public InternetAddress toInternetAddress()
    {
        return emailAddress.toInternetAddress(personalName);
    }
}
