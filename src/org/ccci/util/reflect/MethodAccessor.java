package org.ccci.util.reflect;

import java.beans.Introspector;
import java.lang.reflect.Method;

import org.ccci.util.Exceptions;

import com.google.common.base.Preconditions;

public class MethodAccessor implements Accessor
{

    Method method;
    
    public MethodAccessor(Method method)
    {
        Preconditions.checkArgument(method.getParameterTypes().length == 0, "Method %s is not a no-arg method", method);
        this.method = method;
    }

    @Override
    public Object get(Object object)
    {
        try
        {
            return method.invoke(object);
        }
        catch (Exception e)
        {
            throw Exceptions.wrap(e);
        }
    }

    @Override
    public String getName()
    {
        return toProperty(method.getName());
    }

    private String toProperty(String name)
    {
        if (name.startsWith("get"))
        {
            return Introspector.decapitalize(name.substring(3));
        }
        if (name.startsWith("is"))
        {
            return Introspector.decapitalize(name.substring(2));
        }
        return name;
    }

}
