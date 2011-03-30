package org.ccci.testutils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.google.common.collect.Lists;

public class MockQuery implements Query
{

    @Override
    public int executeUpdate()
    {
        return 0;
    }

    @Override
    public List<?> getResultList()
    {
        return Lists.newArrayList();
    }

    @Override
    public Object getSingleResult()
    {
        return null;
    }

    @Override
    public Query setFirstResult(int startPosition)
    {
        return this;
    }

    @Override
    public Query setFlushMode(FlushModeType flushMode)
    {
        return this;
    }

    @Override
    public Query setHint(String hintName, Object value)
    {
        return this;
    }

    @Override
    public Query setMaxResults(int maxResult)
    {
        return this;
    }

    @Override
    public Query setParameter(String name, Object value)
    {
        return this;
    }

    @Override
    public Query setParameter(int position, Object value)
    {
        return this;
    }

    @Override
    public Query setParameter(String name, Date value, TemporalType temporalType)
    {
        return this;
    }

    @Override
    public Query setParameter(String name, Calendar value, TemporalType temporalType)
    {
        return this;
    }

    @Override
    public Query setParameter(int position, Date value, TemporalType temporalType)
    {
        return this;
    }

    @Override
    public Query setParameter(int position, Calendar value, TemporalType temporalType)
    {
        return this;
    }

    @Override
    public int getMaxResults()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFirstResult()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getHints()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Query setParameter(Parameter<T> param, T value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Query setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Query setParameter(Parameter<Date> param, Date value, TemporalType temporalType)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Parameter<?>> getParameters()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Parameter<?> getParameter(String name)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Parameter<T> getParameter(String name, Class<T> type)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Parameter<?> getParameter(int position)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Parameter<T> getParameter(int position, Class<T> type)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isBound(Parameter<?> param)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getParameterValue(Parameter<T> param)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParameterValue(String name)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParameterValue(int position)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public FlushModeType getFlushMode()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Query setLockMode(LockModeType lockMode)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public LockModeType getLockMode()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> cls)
    {
        throw new UnsupportedOperationException();
    }

    
}
