package org.ccci.testutils;


import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;

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

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refresh(Object entity, Map<String, Object> properties)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void detach(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public LockModeType getLockMode(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProperty(String propertyName, Object value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getProperties()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> cls)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Metamodel getMetamodel()
    {
        throw new UnsupportedOperationException();
    }
    
    

}
