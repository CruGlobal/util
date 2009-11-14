package org.ccci.util;

public interface Factory<T>
{

    /**
     * Creates a <tt>T</tt> from the given string
     * @param value a properly formatted String
     * @return
     * @throws NullPointerException if {@code value} is null
     * @throws IllegalArgumentException if {@code value} is not properly formatted.  Its message is usable for displaying error messages.
     */
    T valueOf(String value);
}
