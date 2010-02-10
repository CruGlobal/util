package org.ccci.util;


/**
 * Indicates that the current state of the request/system/etc is not valid, and the currently executing code 
 * won't (or can't) recover gracefully.  The intent is that this exception would be used for circumstances
 * that are highly unlikely to occur, and so recovery code is not worth the effort.
 * 
 * If the situation is really something that *can't* occur, an {@link AssertionError} should be used instead.
 *  
 * @author Matt Drees
 *
 */
public class UnexpectedStateException extends RuntimeException
{

    public UnexpectedStateException()
    {
    }

    public UnexpectedStateException(String message, Object... args)
    {
        super(String.format(message, args));
    }
    
    public UnexpectedStateException(String message, Throwable cause, Object... args)
    {
        super(String.format(message, args), cause);
    }


    public UnexpectedStateException(Throwable cause)
    {
        super(cause);
    }


    private static final long serialVersionUID = 1L;

}
