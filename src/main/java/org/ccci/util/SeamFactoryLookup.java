package org.ccci.util;

import org.jboss.seam.Component;

/**
 * Looks up seam components by name using {@link Component#getInstance(String)}.
 * 
 * @author Matt Drees
 * 
 */
public class SeamFactoryLookup<T> implements Lookup<T>
{

    private final Class<T> expectedType;

    public SeamFactoryLookup(Class<T> expectedType)
    {
        this.expectedType = expectedType;
    }

    @Override
    public T lookup(String name)
    {
        return expectedType.cast(Component.getInstance(name));
    }

}
