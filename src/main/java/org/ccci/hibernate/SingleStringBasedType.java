package org.ccci.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.ccci.util.Construction;
import org.ccci.util.Factory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

public abstract class SingleStringBasedType<T extends Serializable> extends ValueObjectType<T>
{
    private static final int[] SQL_TYPES = { Hibernate.STRING.sqlType() };

    private final Factory<T> factory;
    
    public SingleStringBasedType() 
    {
        factory = Construction.getFactory(returnedClass());
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
        String valueAsString = (String) Hibernate.STRING.nullSafeGet(resultSet, names[0]);
        return construct(valueAsString);
    }

    private T construct(String valueAsString)
    {
        return factory.valueOf(valueAsString);
    }
    
    @Override
    final public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index)
        throws HibernateException, SQLException
    {
        checkValueType(value);
        Hibernate.STRING.nullSafeSet(preparedStatement, value.toString(), index);
    }

    private static final long serialVersionUID = 1L;
}
