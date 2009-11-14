package org.ccci.model;

import java.io.Serializable;

import org.ccci.util.Patterns;
import org.ccci.util.ValueObject;
import org.ccci.util.contract.Preconditions;

/**
 * Represents a 7-digit peopleid
 * 
 * @author Matt Drees
 */
public class PeopleId extends ValueObject implements Serializable
{

    public static final int MAX_LENGTH = 9;
    
    private final String id;

    public PeopleId(String id)
    {
        Preconditions.checkNotNull(id, "id is null");
        Preconditions.checkArgumentMaxLength(id, MAX_LENGTH, "id");
        Preconditions.checkArgument(id.matches(Patterns.NUMERIC), "id");
        this.id = id;
    }
    
    @Override
    public String toString()
    {
        return id;
    }

    @Override
    protected Object[] getComponents()
    {
        return new Object[]{id};
    }

    private static final long serialVersionUID = 1L;
}
