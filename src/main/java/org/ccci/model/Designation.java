package org.ccci.model;

import java.io.Serializable;

import org.ccci.util.Patterns;
import org.ccci.util.ValueObject;
import org.ccci.util.contract.Preconditions;

/**
 * Represents a 7-digit designation
 * 
 * TODO: rename this DesignationNumber
 * 
 * @author Matt Drees
 */
public class Designation extends ValueObject implements Serializable
{

    public static final int MAX_LENGTH = 7;
    
    private final String designation;

    public Designation(String designation)
    {
        Preconditions.checkNotNull(designation, "designation is null");
        Preconditions.checkArgumentMaxLength(designation, MAX_LENGTH, "designation");
        Preconditions.checkArgument(designation.matches(Patterns.NUMERIC), "designation");
        this.designation = designation;
    }
    
    @Override
    public String toString()
    {
        return designation;
    }

    @Override
    protected Object[] getComponents()
    {
        return new Object[]{designation};
    }

    private static final long serialVersionUID = 1L;

    public static Designation nullableValueOf(String designation)
    {
        return designation == null ? null : new Designation(designation);
    }
}
