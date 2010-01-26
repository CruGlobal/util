package org.ccci.hibernate;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.ccci.util.Exceptions;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

public abstract class SingleBigDecimalBasedType<T extends Serializable & RepresentableAsBigDecimal> extends ValueObjectType<T>
{
    private static final int[] SQL_TYPES = { Hibernate.BIG_DECIMAL.sqlType() };

    private final Constructor<T> constructor;
    
    public SingleBigDecimalBasedType() 
    {
        try
        {
            constructor = returnedClass().getConstructor(BigDecimal.class);
        }
        catch (Exception e)
        {
            throw Exceptions.wrap(e);
        }
    }
    
    @Override
    final public int[] sqlTypes()
    {
        return Arrays.copyOf(SQL_TYPES, SQL_TYPES.length);
    }

    @Override
    final public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
        throws HibernateException, SQLException
    {
        BigDecimal valueAsBigDecimal = (BigDecimal) Hibernate.BIG_DECIMAL.nullSafeGet(resultSet, names[0]);
        return valueAsBigDecimal == null ? null : construct(valueAsBigDecimal);
    }

    private T construct(BigDecimal valueAsBigDecimal)
    {
        try
        {
            return constructor.newInstance(valueAsBigDecimal);
        }
        catch (Exception e)
        {
            throw Exceptions.wrap(e);
        }
    }
    
    @Override
    final public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index)
        throws HibernateException, SQLException
    {
        checkValueType(value);
        Hibernate.BIG_DECIMAL.nullSafeSet(preparedStatement, value == null ? null : 
            ((RepresentableAsBigDecimal)value).toBigDecimal(), index);
    }

    private static final long serialVersionUID = 1L;
}
