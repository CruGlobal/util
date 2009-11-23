package org.ccci.util;

/**
 * Indicates that a given piece of code has not yet been implemented.
 * 
 * @author Matt Drees
 */
public class NotImplementedException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public NotImplementedException()
    {
        super();
    }

    public NotImplementedException(String message)
    {
        super(message);
    }

    
}
