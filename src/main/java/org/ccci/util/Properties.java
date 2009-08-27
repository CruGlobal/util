package org.ccci.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.util.Reflections;

public class Properties
{

    /**
     * Collects all unique annotations that are attached to a field with the same name as the given {@code property}, 
     * and also all annotations attached to the getter/setter methods for the given {@code property}.
     * 
     * Checks superclass methods, too, since annotations might not be inherited.
     * 
     * 
     * @param clazz
     * @param property
     * @return
     */
    public static Set<Annotation> getAllAnnotationsForProperty(Class<?> clazz, String property)
    {
        Set<Annotation> annotations = new HashSet<Annotation>();
    	
    	try {
    		Field field = Reflections.getField(clazz, property);
    		annotations.addAll(Arrays.asList(field.getAnnotations()));
    	} catch (IllegalArgumentException e) {
    		//probably no such field
    	}
    	
    	//annotations might not be inherited, so climb the hierarchy
    	for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
    		try {
    			Method setter = Reflections.getSetterMethod(c, property);
    			annotations.addAll(Arrays.asList(setter.getAnnotations()));
    		} catch (IllegalArgumentException e) {
    			//probably no such setter
    		}
    	}
    	
    	for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
    		try {
    			Method getter = Reflections.getGetterMethod(c, property);
    			annotations.addAll(Arrays.asList(getter.getAnnotations()));
    		} catch (IllegalArgumentException e) {
    			//probably no such getter
    		}
    	}
        return annotations;
    }

    /**
     * 
     * @param <T>
     * @param clazz
     * @param propertyName
     * @param annotationClass
     * @return
     */
    public static <T extends Annotation> T getAnnotationForProperty(Class<?> clazz, String propertyName,
                                                                    Class<T> annotationClass)
    {
        Set<Annotation> annotations = getAllAnnotationsForProperty(clazz, propertyName);
        T found = null;
        for (Annotation annotation : annotations)
        {
            if (annotationClass.isInstance(annotation))
            {
                if (found != null) { throw new IllegalArgumentException(String.format(
                    "More than one annotation of type %s found for %s.%s; one is %s, the other is %s", annotationClass, clazz.getSimpleName(),
                    propertyName, found, annotation)); }
                found = annotationClass.cast(annotation);
            }
        }
        return found;
    }

}
