package org.ccci.debug;

import java.io.Serializable;

import com.google.common.base.Objects;

/**
 * Value object representing an exception class 
 * and a stack frame where the exception was created.
 * @author Matt Drees
 *
 */
public class ExceptionLocation implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final Class<? extends Throwable> throwableClass;
    private final StackTraceElement location;
    
    public ExceptionLocation(Throwable rootThrowable)
    {
        throwableClass = rootThrowable.getClass();
        StackTraceElement[] stackTraceElements = rootThrowable.getStackTrace();
        location = stackTraceElements.length == 0 ? null : stackTraceElements[0];
    }
    
    
    public Class<? extends Throwable> getThrowableClass()
    {
        return throwableClass;
    }

    public StackTraceElement getLocation()
    {
        return location;
    }



    @Override
    public String toString()
    {
        return String.format("%s[throwableClass: %s, location: %s]", 
            this.getClass().getSimpleName(), throwableClass, location);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(throwableClass, location);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ExceptionLocation other = (ExceptionLocation) obj;
        return Objects.equal(this.throwableClass, other.throwableClass) &&
            Objects.equal(this.location, other.location);
    }
}
