package org.ccci.util;

/**
 * A provider of <tt>T</tt>s.
 * 
 * @author Matt Drees
 *
 * @param <T>
 */
public interface Instance<T>
{
    T get();
}
