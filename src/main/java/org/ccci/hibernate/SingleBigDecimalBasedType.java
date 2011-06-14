package org.ccci.hibernate;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.BigDecimalType;

import com.google.common.base.Throwables;

/**
 * 
 * Note: this requires Hibernate 3.6 or higher
 * 
 * @author Matt Drees
 *
 * @param <T>
 */
public abstract class SingleBigDecimalBasedType<T extends Serializable & RepresentableAsBigDecimal> 
    extends ValueObjectType<T>
    implements Serializable
{
    private static final int[] SQL_TYPES = { BigDecimalType.INSTANCE.sqlType() };

    public SingleBigDecimalBasedType() 
    {
        try
        {
            getConstructor();
        }
        catch (Exception e)
        {
            throw Throwables.propagate(e);
        }
    }

    private Constructor<T> getConstructor() throws NoSuchMethodException
    {
        return returnedClass().getConstructor(BigDecimal.class);
    }
    
    @Override
    final public int[] sqlTypes()
    {
        return Arrays.copyOf(SQL_TYPES, SQL_TYPES.length);
    }

    @Override
    final public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner)
        throws HibernateException, SQLException
    {
        BigDecimal valueAsBigDecimal = (BigDecimal) BigDecimalType.INSTANCE.nullSafeGet(resultSet, names[0], session);
        return valueAsBigDecimal == null ? null : construct(valueAsBigDecimal);
    }

    private T construct(BigDecimal valueAsBigDecimal)
    {
        try
        {
            return getConstructor().newInstance(valueAsBigDecimal);
        }
        catch (Exception e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    @Override
    final public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index, SessionImplementor session)
        throws HibernateException, SQLException
    {
        checkValueType(value);
        BigDecimalType.INSTANCE.nullSafeSet(preparedStatement, value == null ? null : 
            ((RepresentableAsBigDecimal)value).toBigDecimal(), index, session);
    }

    private static final long serialVersionUID = 1L;
}
