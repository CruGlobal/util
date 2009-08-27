package org.ccci.util.io;

import java.io.InputStream;

public interface ResourceStreamSupplier
{
    /**
     * Should look up the given resource in the classpath and servlet context path.  Will not return null.
     * @param name may begin with a "/"
     * @throws IllegalArgumentException if the resource is not available
     */
    InputStream getResourceAsStream(String name);
}
