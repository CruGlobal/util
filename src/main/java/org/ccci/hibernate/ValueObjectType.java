package org.ccci.hibernate;

import java.io.Serializable;

import org.ccci.util.Types;
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
        return Types.getTemplateParameterType(getClass());
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
        if (value != null && !returnedClass().isInstance(value))
        {
            throw new ClassCastException(String.format("%s, of type %s, is not a %s", value, value.getClass().getName(), returnedClass().getName()));
        }
    }
    
    private static final long serialVersionUID = 1L;
}
