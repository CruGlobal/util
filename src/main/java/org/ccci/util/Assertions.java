package org.ccci.util;

/**
 * Assertions are useful when you want to either be clear in your code that something must happen or must not happen.
 * You can use them as double-checks.
 * They are also useful when you know that a particular exception can't be thrown, but the compiler doesn't know this.
 * (For example, calling String.getBytes("UTF-8") will not throw an UnsupportedEncodingException, because UTF-8 is required on
 * all compliant JVMs.) 
 * 
 * 
 * @author Matt Drees
 *
 */
public class Assertions
{

    /**
     * If the given name does not match the given class's (full) name, throws an {@link AssertionError}
     */
    public static void assertNameMatchesClassName(String expectedClassName, Class<?> class1)
    {
        if (!expectedClassName.equals(class1.getName()))
        {
            throw new AssertionError("incorrect name: " + expectedClassName);
        }

    }

    /**
     * Throws an {@link AssertionError} with a message generated from the given message format and parameters.
     * 
     * Returns AssertionError, so that calling code can look like "throw Assertions.error("badness")".
     * This helps the compiler know execution can't proceed.
     * 
     * @param message
     * @param parameters
     * @return does not return
     */
    public static AssertionError error(String message, Object... parameters)
    {
        throw new AssertionError(String.format(message, parameters));
    }
 

    /**
     * Throws an {@link AssertionError} with the given cause.
     * 
     * Declared to return AssertionError, so that calling code can look like "throw Assertions.error("badness")".
     * This helps the compiler know execution can't proceed.
     * 
     * @param cause
     * @param message
     * @param parameters
     * @return does not return
     */
    public static AssertionError error(Throwable cause, String message, Object... parameters)
    {
        AssertionError error = new AssertionError(String.format(message, parameters));
        error.initCause(cause);
        throw error;
    }   

}
