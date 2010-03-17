package org.ccci.util;


import com.google.common.base.Preconditions;

/**
 * If, for example, you need to return two things from a method.
 * 
 * Not intended to be used as a value object. (Doesn't implement hashCode(), etc)
 * @author Matt Drees
 *
 * @param <T>
 */
public class Pair<T>
{
    private final T first;
    private final T second;
    
    private Pair(T first, T second)
    {
        this.first = first;
        this.second = second;
    }

    public T getFirst()
    {
        return first;
    }

    public T getSecond()
    {
        return second;
    }
    
    @Override
    public String toString()
    {
        return "<" + first + ", " + second + ">";
    }

    /**
     * Create a pair of the given non-null objects
     * @param <U>
     * @param first
     * @param second
     * @return
     */
    public static <U> Pair<U> of(U first, U second)
    {
        Preconditions.checkArgument(first != null, "first is null");
        Preconditions.checkArgument(second != null, "second is null");
        return new Pair<U>(first, second);
    }
    
}
