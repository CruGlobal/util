package org.ccci.util;

/**
 * An InconsistentDataException indicates that the persistent data needed to process a given request is not
 * consistent with itself or with the expectations of what constitutes valid data.
 * 
 * As an example, this exception would be appropriate when a column should hold only 'Y' or 'N' values, but a given
 * row contains a '0' instead. The processing code should not be expected to recover from this.
 * 
 * Another example is that a row in one table should indicate that a corresponding row is present in a different
 * table, but no such associated row exists.
 * 
 * 
 * It should be noted that database-level constraints can be used to minimize the possibility of such situations
 * (and, where possible, such constraints *should* be used).  However, database-level constraints are not sometimes
 * possible (for example, when the constraint exists between two different databases).  Other times, database-level
 * constraints are inconvenient. 
 * 
 * 
 * @author Matt Drees
 * 
 */
public class InconsistentDataException extends RuntimeException
{
    
    public InconsistentDataException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InconsistentDataException(String message)
    {
        super(message);
    }

    private static final long serialVersionUID = 1L;

}
