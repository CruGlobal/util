package org.ccci.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Types
{

    /**
     * Gets the type parameter value of a generic superclass.  For example:
     * 
     * abstract class Foo<T> {}
     * 
     * class Bar extends Foo<Baz> {}
     * 
     * In this case, then,
     * getTemplateParameterType(Bar.class) == Baz.class
     * 
     * This method does an unchecked cast, and subclasses can abuse this, though it's unlikely.
     * 
     * @param <T>
     * @param subclass
     * @return
     */
    public static <T> Class<T> getTemplateParameterType(Class<?> subclass)
    {
        ParameterizedType type = (ParameterizedType) subclass.getGenericSuperclass();
        Type[] actualTypeArguments = type.getActualTypeArguments();
        @SuppressWarnings("unchecked") //as long as subclass obeys the rules, this should be safe
        Class<T> templateParameterType = (Class<T>) actualTypeArguments[0];
        return templateParameterType;
    }
}
