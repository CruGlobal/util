package org.ccci.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * 
 * @author Matt Drees
 *
 * @param <T> a value object type.  That is, it is immutable and implements {@link #equals(Object)}, {@link #hashCode()}, and {@link #toString()}.
 */
public abstract class ValueObjectType<T extends Serializable> implements UserType
{

    @Override
    final public Class<T> returnedClass()
    {
        return getTemplateParameterType();
    }

    private Class<T> getTemplateParameterType()
    {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] actualTypeArguments = type.getActualTypeArguments();
        @SuppressWarnings("unchecked") //as long as subclass obeys the rules, this should be safe
        Class<T> templateParameterType = (Class<T>) actualTypeArguments[0];
        return templateParameterType;
    }
    

    @Override
    final public boolean isMutable()
    {
        return false;
    }

    @Override
    final public boolean equals(Object x, Object y)
        throws HibernateException
    {
        if (x == y)
        {
            return true;
        }
        else if (x == null || y == null)
        {
            return false;
        }
        else
        {
            return x.equals(y);
        }
    }

    @Override
    final public Object deepCopy(Object value)
        throws HibernateException
    {
        return value;
    }

    @Override
    final public Object assemble(Serializable cached, Object owner)
        throws HibernateException
    {
        return cached;
    }

    @Override
    final public Serializable disassemble(Object value)
        throws HibernateException
    {
        return (Serializable) value;
    }

    @Override
    final public int hashCode(Object x)
        throws HibernateException
    {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    final public Object replace(Object original, Object target, Object owner)
        throws HibernateException
    {
        return original;
    }
    

    /**
     * Verifies that the given object is a {@code T}.  A utility for subclasses.
     * @param value may be null
     * @throws ClassCastException if {@code value} is not a {@code T}
     */
    protected void checkValueType(Object value)
    {
        returnedClass().cast(value);
    }
    
    private static final long serialVersionUID = 1L;
}
