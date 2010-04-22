package org.ccci.util;

import org.ccci.annotations.NotThreadSafe;

@NotThreadSafe
public class Counter
{

    private int counter = 0;
    
    public int next()
    {
        return ++counter;
    }

    public void reset()
    {
        counter = 0;
    }
}
