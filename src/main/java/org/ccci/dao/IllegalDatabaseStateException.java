package org.ccci.dao;

/**
 * An exception thrown when data is loaded from the database that is invalid. 
 * Sometimes an application can recover from this, but generally not.
 * 
 * @author Matt Drees
 *
 */
public class IllegalDatabaseStateException extends RuntimeException
{

    public IllegalDatabaseStateException()
    {
        super();
    }

    public IllegalDatabaseStateException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public IllegalDatabaseStateException(String s)
    {
        super(s);
    }

    public IllegalDatabaseStateException(Throwable cause)
    {
        super(cause);
    }

    private static final long serialVersionUID = 1L;

}
