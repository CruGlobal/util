package org.ccci.util;

import org.ccci.annotations.NotThreadSafe;

@NotThreadSafe
public class Counter
{

    private int counter;
    private final int initialValue;
    
    
    /**
     * Creates a counter with the given initial value.  This value will
     * be the first number returned by {@link #next()}.
     */
    public static Counter startingAt(int initialValue)
    {
        return new Counter(initialValue);
    }

    private Counter(int initialValue)
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

    public int next()
    {
        return counter++;
    }

    public void reset()
    {
        counter = initialValue;
    }
}
