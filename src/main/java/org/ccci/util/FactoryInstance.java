package org.ccci.util;

import org.jboss.seam.Component;

public class FactoryInstance<T> implements Instance<T>
{

    private final Class<T> expectedType;
    private final String seamVariableName;

    public FactoryInstance(Class<T> expectedType, String seamVariableName)
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
