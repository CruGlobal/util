package org.ccci.model;

import java.io.Serializable;

import org.ccci.util.ValueObject;
import org.ccci.util.contract.Preconditions;


/**
 * Represents a GCX/SSO Username.
 * 
 * @author Matt Drees
 *
 */
public class SsoUsername extends ValueObject implements Serializable
{

    /** TODO: figure out what the max length is */
    public static final int MAX_LENGTH = Integer.MAX_VALUE;
    
    private final String username;
    
    public SsoUsername(String username)
    {
        Preconditions.checkNotNull(username, "username is null");
        Preconditions.checkArgumentMaxLength(username, MAX_LENGTH, "username");
        this.username = username;
    }
    
    @Override
    public String toString()
    {
        return username;
    }

    @Override
    protected Object[] getComponents()
    {
        return new Object[]{username};
    }

    private static final long serialVersionUID = 1L;

    public static SsoUsername nullableValueOf(String username)
    {
        return username == null ? null : new SsoUsername(username);
    }
}
