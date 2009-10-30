package org.ccci.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

/**
 * 
 * @author Matt Drees
 *
 * @param <T> a value object type.  That is, it is immutable and implements {@link #equals(Object)}, {@link #hashCode()}, and {@link #toString()}.
 */
public abstract class CompositeValueObjectType<T extends Serializable> implements CompositeUserType
{

    private final String[] propertyNames;
    private final Type[] propertyTypes;
    
    public CompositeValueObjectType(String[] propertyNames, Type[] propertyTypes)
    {
        this.propertyNames = propertyNames;
        this.propertyTypes = propertyTypes;
    }

    @Override
    final public Class<T> returnedClass()
    {
        return getTemplateParameterType();
    }

    private Class<T> getTemplateParameterType()
    {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        java.lang.reflect.Type[] actualTypeArguments = type.getActualTypeArguments();
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
    final public int hashCode(Object x)
        throws HibernateException
    {
        return x == null ? 0 : x.hashCode();
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

    @Override
    public Object assemble(Serializable cached, SessionImplementor session, Object owner)
        throws HibernateException
    {
        return cached;
    }

    @Override
    public Serializable disassemble(Object value, SessionImplementor session)
        throws HibernateException
    {
        return (Serializable) value;
    }

    @Override
    public Object replace(Object original, Object target, SessionImplementor session, Object owner)
        throws HibernateException
    {
        return original;
    }

    @Override
    public void setPropertyValue(Object component, int index, Object value)
        throws HibernateException
    {
        throw new UnsupportedOperationException("This type is not mutable!");
    }


    @Override
    public String[] getPropertyNames()
    {
        return Arrays.copyOf(propertyNames, propertyNames.length);
    }

    @Override
    public Type[] getPropertyTypes()
    {
        return Arrays.copyOf(propertyTypes, propertyTypes.length);
    }
}
