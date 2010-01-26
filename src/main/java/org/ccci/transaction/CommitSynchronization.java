package org.ccci.transaction;

import javax.transaction.Status;
import javax.transaction.Synchronization;

/**
 * A convenience class for methods that need special rollback logic.
 * 
 * @author Matt Drees
 *
 */
public abstract class CommitSynchronization implements Synchronization
{

    public abstract void afterCommit();
    
    
    @Override
    public void afterCompletion(int status)
    {
        if (status == Status.STATUS_COMMITTED)
        {
            afterCommit();
        }
    }

    @Override
    public void beforeCompletion()
    {
    }

}
