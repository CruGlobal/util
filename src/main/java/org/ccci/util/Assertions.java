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

    public static Error error(String message, Object... parameters)
    {
        throw new AssertionError(String.format(message, parameters));
    }

}
