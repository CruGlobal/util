package org.ccci.util;


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
     * Returns AssertionError, so that calling code can look like "throw Assertions.error("badness")".
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
