package org.ccci.util;


/**
 * Always returns the instance given at construction time.
 * 
 * @author Matt Drees
 */
public class SingleValueLookup<T> implements Lookup<T>
{

    private final T instance;

    public SingleValueLookup(T instance)
    {
        this.instance = instance;
    }

    @Override
    public T lookup(String name)
    {
        return instance;
    }

}
