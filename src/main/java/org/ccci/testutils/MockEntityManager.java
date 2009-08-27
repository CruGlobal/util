package org.ccci.testutils;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import com.google.common.collect.Lists;

public class MockEntityManager implements EntityManager
{

    private boolean wasFlushed;
    private boolean wasCleared;
    private boolean wasClosed;
    private boolean wasTransactionJoined;
    private List<Object> persistedEntities = Lists.newArrayList();
    private List<Object> mergedEntities = Lists.newArrayList();
    private List<Object> refreshedEntities = Lists.newArrayList();
    private List<Object> removedEntities = Lists.newArrayList();
    private List<Object> managedEntities = Lists.newArrayList();
    private FlushModeType setFlushMode;

    public void clear()
    {
        wasCleared = true;
    }

    public void close()
    {
        wasClosed = true;
    }

    public boolean contains(Object entity)
    {
        return managedEntities.contains(entity) || persistedEntities.contains(entity);
    }

    public Query createNamedQuery(String queryName)
    {
        return null;
    }

    public Query createNativeQuery(String queryString)
    {
        return null;
    }

    @SuppressWarnings("unchecked")
    public Query createNativeQuery(String arg0, Class arg1)
    {
        return null;
    }

    public Query createNativeQuery(String arg0, String arg1)
    {
        return null;
    }

    public Query createQuery(String arg0)
    {
        return null;
    }

    public <T> T find(Class<T> arg0, Object arg1)
    {
        return null;
    }

    public void flush()
    {
        wasFlushed = true;
    }

    public Object getDelegate()
    {
        return null;
    }

    public FlushModeType getFlushMode()
    {
        return null;
    }

    public <T> T getReference(Class<T> arg0, Object arg1)
    {
        return null;
    }

    public EntityTransaction getTransaction()
    {
        return null;
    }

    public boolean isOpen()
    {
        return false;
    }

    public void joinTransaction()
    {
        wasTransactionJoined = true;
    }

    public void lock(Object arg0, LockModeType arg1)
    {
        
    }

    public <T> T merge(T entity)
    {
        mergedEntities.add(entity);
        return entity;
    }

    public void persist(Object entity)
    {
        persistedEntities.add(entity);
        managedEntities.add(entity);
    }

    public void refresh(Object entity)
    {
        refreshedEntities.add(entity);
    }

    public void remove(Object entity)
    {
        removedEntities.add(entity);
        managedEntities.remove(entity);
    }

    public void setFlushMode(FlushModeType flushMode)
    {
        setFlushMode = flushMode;
    }

    public boolean wasFlushed()
    {
        return wasFlushed;
    }

    public boolean wasCleared()
    {
        return wasCleared;
    }

    public boolean wasClosed()
    {
        return wasClosed;
    }

    public boolean wasTransactionJoined()
    {
        return wasTransactionJoined;
    }

    public List<Object> getPersistedEntities()
    {
        return persistedEntities;
    }

    public List<Object> getMergedEntities()
    {
        return mergedEntities;
    }

    public List<Object> getRefreshedEntities()
    {
        return refreshedEntities;
    }

    public List<Object> getRemovedEntities()
    {
        return removedEntities;
    }

    public FlushModeType getSetFlushMode()
    {
        return setFlushMode;
    }

    public void addManagedEntity(Object object)
    {
        managedEntities.add(object);
    }

}
