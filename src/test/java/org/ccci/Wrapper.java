package org.ccci;

/**
 * A class meant to be used when creating an anonymous inner class
 * and a variable must be modified in an enclosing scope.  Java 
 * only allows accessing final variables, so use a final Wrapper<T>
 * and modify its value via {@link #set(Object)}.
 * 
 * @author Matt Drees
 *
 * @param <T>
 */
public class Wrapper<T>
{

    public Wrapper(T value)
    {
        this.value = value;
    }
    
    private T value;
    
    public void set(T value)
    {
        this.value = value;
    }
    
    public T get()
    {
        return value;
    }

}
