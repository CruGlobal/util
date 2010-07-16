package org.ccci.util;

import org.jboss.seam.Component;

/**
 * A provider of <tt>T</tt>s that are indexed by name.
 * 
 * This is intended mostly to help classes be more easily unit testable.  So, instead of calling {@link Component#getInstance(String)} directly,
 * a class could use a {@link Lookup} which in unit tests can be more easily mocked.  In production, the class could use {@link SeamFactoryLookup}.
 * 
 * @author Matt Drees
 *
 * @param <T> the expected type of the objects to be looked up.
 */
public interface Lookup<T>
{
    T lookup(String name);
}
