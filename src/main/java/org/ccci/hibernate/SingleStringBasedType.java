package org.ccci.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.ccci.util.Construction;
import org.ccci.util.Factory;
import org.hibernate.HibernateException;
import org.hibernate.type.StringType;

/**
 * Note: this requires Hibernate 3.6 or higher
 * 
 * @author Matt Drees
 *
 * @param <T>
 */
public abstract class SingleStringBasedType<T extends Serializable> 
    extends ValueObjectType<T> 
    implements Serializable
{
    private static final int[] SQL_TYPES = { StringType.INSTANCE.sqlType() };

    public SingleStringBasedType() 
    {
        getFactory();
    }

    private Factory<T> getFactory()
    {
        return Construction.getFactory(returnedClass());
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
        String valueAsString = (String) StringType.INSTANCE.nullSafeGet(resultSet, names[0]);
        return valueAsString == null ? null : construct(valueAsString);
    }

    private T construct(String valueAsString)
    {
        return getFactory().valueOf(valueAsString);
    }
    
    @Override
    final public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index)
        throws HibernateException, SQLException
    {
        checkValueType(value);
        StringType.INSTANCE.nullSafeSet(preparedStatement, value == null ? null : value.toString(), index);
    }

    private static final long serialVersionUID = 1L;
}
