package org.ccci.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.ccci.util.Exceptions;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

/**
 * Sometimes third party frameworks aren't designed with your convenience in mind.
 * 
 * Use at your own risk.  Only runtime exceptions are thrown for the convenience of client code.
 * 
 * @author Matt Drees
 */
public class Reflections
{
   

    /**
     * Uses reflection to set a (potentially private) non-static field to the given value. 
     * 
     * @param instance the object instance whose private field should be changed
     * @param fieldOwner the class that declares the target field
     * @param fieldname the name of the target field
     * @param value the new value
     */
    public static void setField(Object instance, Class<?> fieldOwner, String fieldname, Object value)
    {
        Preconditions.checkArgument(fieldOwner.isInstance(instance), "%s is not a %s", instance, fieldOwner);
        Field target;
        try
        {
            target = fieldOwner.getDeclaredField(fieldname);
        }
        catch (NoSuchFieldException e)
        {
            throw Exceptions.newIllegalArgumentException("%s has no field named %s", instance, fieldname);
        }
        boolean alreadyAccessible = target.isAccessible();
        if (!alreadyAccessible)
        {
            target.setAccessible(true);
        }
        try
        {
            target.set(instance, value);
        }
        catch (IllegalAccessException e)
        {
            throw Throwables.propagate(e);
        }
        finally
        {
            if (!alreadyAccessible)
            {
                target.setAccessible(false);
            }
        }
    }

    public static <T> T getStaticFieldValue(Class<?> fieldOwner, String fieldname)
    {
        return getFieldInternal(null, fieldOwner, fieldname);
    }


    public static <T> T getField(Object instance, Class<?> fieldOwner, String fieldname)
    {
        Preconditions.checkNotNull(instance, "instance is null");
        Preconditions.checkArgument(fieldOwner.isInstance(instance), "%s is not a %s", instance, fieldOwner);
        return getFieldInternal(instance, fieldOwner, fieldname);
    }

    private static <T> T getFieldInternal(Object instance, Class<?> fieldOwner, String fieldname)
    {
        Field target;
        try
        {
            target = fieldOwner.getDeclaredField(fieldname);
        }
        catch (NoSuchFieldException e)
        {
            throw Exceptions.newIllegalArgumentException("%s has no field named %s", instance, fieldname);
        }
        boolean alreadyAccessible = target.isAccessible();
        if (!alreadyAccessible)
        {
            target.setAccessible(true);
        }
        try
        {

            @SuppressWarnings("unchecked") //we've already left statically-typed land, 
            // so we may as well make it convenient for callers
            T value = (T) target.get(instance);
            return value;
        }
        catch (IllegalAccessException e)
        {
            throw Throwables.propagate(e);
        }
        finally
        {
            if (!alreadyAccessible)
            {
                target.setAccessible(false);
            }
        }
    }

    
    public static <T> T invokeNoArgMethod(Object instance, Class<?> methodOwner, String methodName)
    {
        return Reflections.<T>invokeSpecificMethod(instance, methodOwner, methodName).withArguments();
    }

    /**
     * This is a shortcut for {@link #invokeSpecificMethod(Object, Class, String, Class...)}.{@link InvocationTarget#withArguments(Object...)}.
     * That is, it uses the types of the given arguments to look up the method.  This will only work if the types are
     * exact matches for the parameter types.  
     * @param <T>
     * @param instance
     * @param methodOwner
     * @param methodName
     * @param args
     * @return
     */
    public static <T> T invokeMethod(Object instance, Class<?> methodOwner, String methodName, Object... args)
    {
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++)
        {
            types[i] = args[i].getClass();
        }
        return Reflections.<T>invokeSpecificMethod(instance, methodOwner, methodName, types).withArguments(args);
    }
    
    public static <T> InvocationTarget<T> invokeSpecificMethod(Object instance, Class<?> methodOwner, String methodName, Class<?>... argTypes)
    {
        Preconditions.checkArgument(methodOwner.isInstance(instance), "%s is not a %s", instance, methodOwner);
        Method target;
        try
        {
            target = methodOwner.getDeclaredMethod(methodName, argTypes);
        }
        catch (NoSuchMethodException e)
        {
            throw Exceptions.newIllegalArgumentException("%s has no method named %s and with argument types %s", 
                instance.getClass(), methodName, Arrays.toString(argTypes));
        }
        return new InvocationTarget<T>(target, instance);
    }

    public static class InvocationTarget<T>
    {
        private final Method target;
        private final Object instance;
        
        public InvocationTarget(Method target, Object instance)
        {
            this.target = target;
            this.instance = instance;
        }

        public T withArguments(Object... arguments)
        {

            boolean alreadyAccessible = target.isAccessible();
            if (!alreadyAccessible)
            {
                target.setAccessible(true);
            }
            try
            {
                @SuppressWarnings("unchecked") //we've already left statically-typed land, 
                // so we may as well make it convenient for callers
                T result = (T) target.invoke(instance, arguments);
                return result;
            }
            catch (IllegalAccessException e)
            {
                throw Throwables.propagate(e);
            }
            catch (InvocationTargetException e)
            {
                throw Throwables.propagate(e.getCause());
            }
            finally
            {
                if (!alreadyAccessible)
                {
                    target.setAccessible(false);
                }
            }    
        }
        
    }

    
    
    
}
