package org.ccci.model;

import java.io.Serializable;

import org.ccci.util.ValueObject;
import org.ccci.util.contract.Preconditions;


/**
 * Represents a GCX/SSO Globally Unique Id.
 * 
 * They typically look like, for example, "BB20A5DB-D31E-65B5-3629-E24504A00942".
 * 
 * @author Matt Drees
 *
 */
public class SsoGuid extends ValueObject implements Serializable
{

    public static final int MAX_LENGTH = 36;
    
    private final String guid;
    
    public SsoGuid(String guid)
    {
        Preconditions.checkNotNull(guid, "guid is null");
        Preconditions.checkArgumentMaxLength(guid, MAX_LENGTH, "guid");
        this.guid = guid;
    }
    
    @Override
    public String toString()
    {
        return guid;
    }

    @Override
    protected Object[] getComponents()
    {
        return new Object[]{guid};
    }

    private static final long serialVersionUID = 1L;
}
