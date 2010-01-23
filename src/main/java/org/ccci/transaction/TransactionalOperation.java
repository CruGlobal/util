package org.ccci.transaction;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.ccci.util.Exceptions;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

/**
 * A utility class for running a block of code in a transaction.  Usage should be something like:
 * <pre><code>
 * new TransactionalOperation(userTransaction)
 * {
 * 
 *    protected void run()
 *    {
 *       //code that forms an atomic operation
 *    }
 * }.executeInTransaction();
 * 
 * </code></pre>
 * 
 * See {@link #executeInTransaction()} for more details on behavior.
 * 
 * @author Matt Drees
 *
 */
public abstract class TransactionalOperation
{
    Log log = Logging.getLog(TransactionalOperation.class);
    
    private final UserTransaction transaction;
    
    public TransactionalOperation(UserTransaction transaction)
    {
        this.transaction = transaction;
    }

    /**
     * If beginning the transaction throws an exception, either that exception or a wrapped version of it is thrown here. 
     * If {@link #run()} throws an exception, that exception is rethrown here.
     * If committing the transaction throws an exception, either that exception or a wrapped version of it is thrown here.
     * If rolling the transaction back throws an exception, it is logged, so that the exception thrown by {@link #run()} can
     * be thrown.  If rolling the transaction back throws an {@link Error}, the error is thrown and the originally thrown exception
     * will be logged.
     */
    public void executeInTransaction()
    {
        try
        {
            transaction.begin();
        }
        catch (NotSupportedException e)
        {
            throw Exceptions.wrap(e);
        }
        catch (SystemException e)
        {
            throw Exceptions.wrap(e);
        }
        
        try
        {
            run();
        }
        catch (RuntimeException e)
        {
            try
            {
                transaction.rollback();
                throw e;
            }
            catch(Exception rollbackException)
            {
                log.error("exception rolling back transaction; status is {0}", 
                    rollbackException, TransactionStatuses.getStatusAsString(transaction));
                throw e;
            }
            catch(Error error)
            {
                log.error("error rolling back transaction, and will be mask original exception; original exception was:", e);
                throw error;
            }
        }
        
        try
        {
            transaction.commit();
        }
        catch (Exception commitException)
        {
            log.error("exception committing transaction; status is {0}", 
                commitException, TransactionStatuses.getStatusAsString(transaction));
            throw Exceptions.wrap(commitException);
        }
    }
    
    /**
     * The method to run in the transaction.  Should not normally throw exceptions.
     */
    abstract protected void run();
    
}
