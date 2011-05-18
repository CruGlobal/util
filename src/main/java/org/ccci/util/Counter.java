package org.ccci.util;

import org.ccci.annotations.NotThreadSafe;

@NotThreadSafe
public class Counter
{

    private long counter;
    private final long initialValue;
    
    
    /**
     * Creates a counter with the given initial value.  This value will
     * be the first number returned by {@link #next()}.
     */
    public static Counter startingAt(int initialValue)
    {
        return new Counter(initialValue);
    }

    private Counter(long initialValue)
    {
        this.initialValue = initialValue;
        this.counter = initialValue;
    }
    
    /**
     * Creates a counter whose first value is 1
     */
    public Counter()
    {
        this(1);
    }

    /**
     * Returns the current value of the counter, and then increments it
     */
    public long next()
    {
        return counter++;
    }

    /**
     * Resets this counter to its initial value
     */
    public void reset()
    {
        counter = initialValue;
    }
    
    @Override
    public String toString()
    {
        return String.valueOf(counter);
    }

    public boolean increasedByMultipleOf(long threshold)
    {
        return (counter - initialValue) % threshold == 0; 
    }
}
