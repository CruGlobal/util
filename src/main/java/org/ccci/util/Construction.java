package org.ccci.util;

import java.lang.reflect.Method;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

public class Construction
{

    /**
     * Looks for a {@link ConstructsFromString} annotation on the given class, and returns the method
     * @param type
     * @return
     */
    public static Method getConstructionMethod(Class<?> type)
    {
        Preconditions.checkArgument(type != null, "ValueObjectConverter is not attached to a component with a specified value");
        Method constructionMethod = Iterables.getOnlyElement(Annotations.getMethodsAnnotated(type, ConstructsFromString.class));
        Class<?>[] parameterTypes = constructionMethod.getParameterTypes();
        Preconditions.checkArgument(parameterTypes.length == 1 && parameterTypes[0].equals(String.class),
            "construction method must have 1 string argument; has %s", (Object[]) parameterTypes);
        Class<?> returnType = constructionMethod.getReturnType();
        Preconditions.checkArgument(type.isAssignableFrom(returnType), "construction method must return %s; returns %s",
            type, returnType);
        return constructionMethod;
    }

}
