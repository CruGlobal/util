package org.ccci.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;


class CustomEqualsList<T> extends AbstractList<T>
{

    private class Wrapper
    {
        private T object;

        Wrapper(T object)
        {
            this.object = object;
        }

        @Override
        public boolean equals(Object obj)
        {
            Wrapper wrapper = Classes.cast(Wrapper.class, obj);
            return equalsOverride.isEqual(object, wrapper.object);
        }
    }

    private final List<Wrapper> items = Lists.newArrayList();
    private final EqualsOverride<T> equalsOverride;
    private final Class<T> elementClass;
    
    public CustomEqualsList(EqualsOverride<T> equalsOverride)
    {
        this.equalsOverride = equalsOverride;
        Type[] genericInterfaces = equalsOverride.getClass().getGenericInterfaces();
        Preconditions.checkArgument(genericInterfaces.length == 1, "equalsOverride can only implement EqualsOverride");
        ParameterizedType type = (ParameterizedType) genericInterfaces[0];
        
        @SuppressWarnings("unchecked")
        Class<T> actualTypeArgument = (Class<T>) type.getActualTypeArguments()[0];
        this.elementClass = actualTypeArgument;
    }

    @Override
    public T get(int index)
    {
        return items.get(index).object;
    }

    @Override
    public T set(int index, T element)
    {
        Wrapper old = items.set(index, new Wrapper(element));
        return old == null ? null : old.object;
    }
    
    @Override
    public boolean add(T e)
    {
        return items.add(new Wrapper(e));
    }
    
    @Override
    public T remove(int index)
    {
        Wrapper removed = items.remove(index);
        return removed == null ? null : removed.object;
    }
    
    @Override
    public int size()
    {
        return items.size();
    }
    
    @Override
    public boolean contains(Object o)
    {
        return items.contains(new Wrapper(Classes.cast(elementClass, o)));
    }
    
    @Override
    public boolean remove(Object o)
    {
        return items.remove(new Wrapper(Classes.cast(elementClass, o)));
    }
}
