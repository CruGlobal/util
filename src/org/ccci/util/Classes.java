package org.ccci.util;

import java.util.Iterator;

import com.google.common.base.Preconditions;

public class Classes
{

    public static class ClassHierarchyIterator implements Iterator<Class<?>>
    {
        private Class<?> currentType;
        
        public ClassHierarchyIterator(Class<?> type)
        {
            currentType = type;
        }

        @Override
        public boolean hasNext()
        {
            return !currentType.equals(Object.class);
        }

        @Override
        public Class<?> next()
        {
            Class<?> next = currentType;
            currentType = currentType.getSuperclass();
            return next;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("cannot remove a classes superclass");
        }

    }

    public static <T> T cast(Class<T> clazz, Object object)
    {
        if (!clazz.isInstance(object)) { throw new ClassCastException("class " + object.getClass().getName()
                + " cannot be cast to a " + clazz.getName()); }
        return clazz.cast(object);
    }
    
    public static boolean isInstanceOf(Object object, Class<?>... types)
    {
        for (Class<?> type : types)
        {
            if (type.isInstance(object))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * An iterable of a class and its successive superclasses.  Does not include Object.class
     * @param type
     * @return
     */
    public static Iterable<Class<?>> classHierarchyOf(final Class<?> type)
    {
        Preconditions.checkNotNull(type, "type is null");
        Preconditions.checkArgument(!type.isInterface() && !type.isPrimitive(), "not a class: %s", type);
        
        return new Iterable<Class<?>>() {

            @Override
            public Iterator<Class<?>> iterator()
            {
                return new ClassHierarchyIterator(type);
            }
            
        };
    }

}
