package org.ccci.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.google.common.collect.Lists;

public class Annotations
{

    /**
     * Get all declared fields (so, includes e.g. private fields) of the given class annotated with <tt>annotationClass</tt>.  Does not include superclasses' fields.
     * 
     * @param target
     * @param annotationClass
     * @return
     */
    public static List<Field> getFieldsAnnotated(Class<?> target, Class<? extends Annotation> annotationClass)
    {
        List<Field> fields = Lists.newArrayList();
        for (Field field : target.getDeclaredFields())
        {
            Annotation annotation = field.getAnnotation(annotationClass);
            if (annotation != null)
            {
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * Get all declared methods (so, includes e.g. private methods) of the given class annotated
     * with <tt>annotationClass</tt>
     * 
     * @param target
     * @param annotationClass
     * @return
     */
    public static List<Method> getMethodsAnnotated(Class<?> target, Class<? extends Annotation> annotationClass)
    {
        List<Method> methods = Lists.newArrayList();
        for (Method method : target.getDeclaredMethods())
        {
            Annotation annotation = method.getAnnotation(annotationClass);
            if (annotation != null)
            {
                methods.add(method);
            }
        }
        return methods;
    }

}
