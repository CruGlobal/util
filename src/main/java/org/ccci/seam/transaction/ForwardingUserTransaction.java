package org.ccci.seam.transaction;

import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;

import org.jboss.seam.transaction.UserTransaction;

public abstract class ForwardingUserTransaction implements UserTransaction
{

    protected abstract UserTransaction delegate();

    public void begin() throws NotSupportedException, SystemException
    {
        delegate().begin();
    }

    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
            SecurityException, IllegalStateException, SystemException
    {
        delegate().commit();
    }

    public void enlist(EntityManager entityManager) throws SystemException
    {
        delegate().enlist(entityManager);
    }

    public int getStatus() throws SystemException
    {
        return delegate().getStatus();
    }

    public boolean isActive() throws SystemException
    {
        return delegate().isActive();
    }

    public boolean isActiveOrMarkedRollback() throws SystemException
    {
        return delegate().isActiveOrMarkedRollback();
    }

    public boolean isCommitted() throws SystemException
    {
        return delegate().isCommitted();
    }

    public boolean isConversationContextRequired()
    {
        return delegate().isConversationContextRequired();
    }

    public boolean isMarkedRollback() throws SystemException
    {
        return delegate().isMarkedRollback();
    }

    public boolean isNoTransaction() throws SystemException
    {
        return delegate().isNoTransaction();
    }

    public boolean isRolledBack() throws SystemException
    {
        return delegate().isRolledBack();
    }

    public boolean isRolledBackOrMarkedRollback() throws SystemException
    {
        return delegate().isRolledBackOrMarkedRollback();
    }

    public void registerSynchronization(Synchronization sync)
    {
        delegate().registerSynchronization(sync);
    }

    public void rollback() throws IllegalStateException, SecurityException, SystemException
    {
        delegate().rollback();
    }

    public void setRollbackOnly() throws IllegalStateException, SystemException
    {
        delegate().setRollbackOnly();
    }

    public void setTransactionTimeout(int arg0) throws SystemException
    {
        delegate().setTransactionTimeout(arg0);
    }
    
}