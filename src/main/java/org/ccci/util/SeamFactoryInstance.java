package org.ccci.util;

import org.jboss.seam.Component;

public class SeamFactoryInstance<T> implements Instance<T>
{

    private final Class<T> expectedType;
    private final String seamVariableName;

    public SeamFactoryInstance(Class<T> expectedType, String seamVariableName)
    {
        this.expectedType = expectedType;
        this.seamVariableName = seamVariableName;
    }

    @Override
    public T get()
    {
        return expectedType.cast(Component.getInstance(seamVariableName));
    }

}
