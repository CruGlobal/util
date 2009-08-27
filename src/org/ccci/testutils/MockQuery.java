package org.ccci.testutils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
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

    
}
