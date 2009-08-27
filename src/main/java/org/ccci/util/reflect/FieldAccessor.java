package org.ccci.util.reflect;

import java.lang.reflect.Field;

import org.ccci.util.Exceptions;

public class FieldAccessor implements Accessor
{

    Field field;
    
    public FieldAccessor(Field field)
    {
        field.setAccessible(true);
        this.field = field;
    }

    @Override
    public Object get(Object object)
    {
        try
        {
            return field.get(object);
        }
        catch (Exception e)
        {
            throw Exceptions.wrap(e);
        }
    }

    @Override
    public String getName()
    {
        return field.getName();
    }

}
