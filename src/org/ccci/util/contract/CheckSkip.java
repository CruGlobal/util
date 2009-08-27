package org.ccci.util.contract;

import java.util.Set;

/**
 * Implementing classes should implement {@link #equals(Object)} and {@link #hashCode()} appropriately, as
 * they will be stored in a {@link Set}.
 * 
 * @see CheckSkippingStrategy
 * @author Matt Drees
 *
 */
public interface CheckSkip
{

    /**
     * returns true if the given check should be skipped
     * @param check
     * @return
     */
    public boolean shouldSkip(Check check);
    
}
