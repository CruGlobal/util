package org.ccci.util;

public class Counter
{

    private int counter = 0;
    
    public int next()
    {
        return ++counter;
    }
}
