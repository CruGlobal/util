package org.ccci.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

/**
 * Contains utilities for constructing classes that are value objects, whose internal state is a String
 * 
 * @author Matt Drees
 */
public class Construction
{


    /**
     * Looks for a {@link ConstructsFromString} annotation on the given class, and returns the method
     * @param type
     * @return a static factory method that takes a single {@link String} as an argument
     * @throws IllegalArgumentException if there are multiple {@link ConstructsFromString} annotated static factory methods, 
     *    or if there is no  {@link ConstructsFromString}-annotated static factory method
     */
    public static Method getStringStaticFactoryMethod(Class<?> type)
    {
        Preconditions.checkArgument(type != null, "ValueObjectConverter is not attached to a component with a specified value");
        Method constructionMethod;
        List<Method> methods = Annotations.getMethodsAnnotated(type, ConstructsFromString.class);
        try
        {
            constructionMethod = Iterables.getOnlyElement(methods);
        }
        catch (NoSuchElementException e)
        {
            throw Exceptions.newIllegalArgumentException("no method of %s is annotated @%s", 
                type, ConstructsFromString.class.getName());
        }
        catch (IllegalArgumentException e)
        {
            throw Exceptions.newIllegalArgumentException("More than one of %s is annotated @%s.  There are %s: %s",
                type, ConstructsFromString.class.getName(), methods.size(), methods);
        }
        Class<?>[] parameterTypes = constructionMethod.getParameterTypes();
        Preconditions.checkArgument(parameterTypes.length == 1 && parameterTypes[0].equals(String.class),
            "construction method must have 1 string argument; has %s", (Object[]) parameterTypes);
        Class<?> returnType = constructionMethod.getReturnType();
        Preconditions.checkArgument(type.isAssignableFrom(returnType), 
            "construction method must return %s; %s, however, returns %s",
            type, constructionMethod, returnType);
        Preconditions.checkArgument(Modifier.isStatic(constructionMethod.getModifiers()), 
            "construction method must be static; %s is not", 
            constructionMethod);
        return constructionMethod;
    }


    /**
     * Returns a suitable {@link Factory} for creating instances of type T, provided T either has a single-String-arg constructor,
     * or a single-String-arg static factory method annotated with {@link ConstructsFromString}
     *  
     * @param <T> a type that can be constructed from a single String
     * @param type
     * @return
     */
    public static <T> Factory<T> getFactory(Class<T> type)
    {
        try
        {
            return new MethodFactory<T>(getStringStaticFactoryMethod(type));
        }
        catch(IllegalArgumentException factoryMethodException)
        {
            Constructor<T> constructor;
            try
            {
                constructor = getStringConstructor(type);
            }
            catch (IllegalArgumentException constructorException)
            {
                throw Exceptions.newIllegalArgumentException(
                    "Class %s has no appropriate construction strategy, for the following reasons:%n-%s%n-%s"
                      , type, factoryMethodException.getMessage(), constructorException.getMessage());
            }
            return new ConstructorFactory<T>(constructor);
        }
    }
    
    public static <T> Constructor<T> getStringConstructor(Class<T> type)
    {
        try
        {
            return type.getConstructor(String.class);
        }
        catch (NoSuchMethodException e)
        {
            throw Exceptions.newIllegalArgumentException(e, "Class %s does not have a single String constructor", type);
        }
    }

    static class MethodFactory<T> implements Factory<T>
    {
        private final Method factoryMethod;
        
        public MethodFactory(Method method)
        {
            this.factoryMethod = method;
        }

        @Override
        public T valueOf(String value)
        {
            try
            {
                @SuppressWarnings("unchecked") //we trust getFactory() to already have checked this!
                T result = (T) factoryMethod.invoke(null, value);
                return result;
            }
            catch (InvocationTargetException e)
            {
                throw unwrapITE(e);
            }
            catch (IllegalAccessException e)
            {
                throw Exceptions.wrap(e);
            }
        }

    }

    //unwrap any runtime exceptions thrown by methods/constructors
    private static RuntimeException unwrapITE(InvocationTargetException e)
    {
        throw Exceptions.wrap(e.getCause());
    }
    

    static class ConstructorFactory<T> implements Factory<T>
    {
        private final Constructor<T> constructor;
        
        public ConstructorFactory(Constructor<T> constructor)
        {
            this.constructor = constructor;
        }

        @Override
        public T valueOf(String value)
        {
            try
            {
                return constructor.newInstance(value);
            }
            catch (InvocationTargetException e)
            {
                throw unwrapITE(e);
            }
            catch (InstantiationException e)
            {
                throw Exceptions.wrap(e);
            }
            catch (IllegalAccessException e)
            {
                throw Exceptions.wrap(e);
            }
        }

    }
}
