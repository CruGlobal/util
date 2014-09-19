package org.ccci.seam.transaction;

import javax.naming.NamingException;
import javax.transaction.SystemException;

import org.ccci.debug.DebugStackTrace;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.transaction.UserTransaction;

/**
 * Adds logging to setRollbackOnly() calls.
 * 
 * @author Matt Drees
 */
@Name("org.jboss.seam.transaction.transaction")
@Scope(ScopeType.EVENT)
@Install(precedence=Install.FRAMEWORK + 1)
@BypassInterceptors
public class TransactionExtension extends Transaction
{
    
    public static class LoggingUserTransaction extends ForwardingUserTransaction implements UserTransaction
    {

        private final Log log = Logging.getLog(LoggingUserTransaction.class);
        private final UserTransaction transaction;

        public LoggingUserTransaction(UserTransaction transaction)
        {
            this.transaction = transaction;
        }

        @Override
        protected UserTransaction delegate()
        {
            return transaction;
        }
        
        @Override
        public void setRollbackOnly() throws IllegalStateException, SystemException
        {
            if (log.isDebugEnabled())
            {
                boolean markedRollback;
                try
                {
                    markedRollback = isMarkedRollback();
                }
                catch (Exception e)
                {
                    log.warn("exception checking if transaction is marked rollback; swallowing", e);
                    markedRollback = true; //not necessarily true, but no reason to log the stacktrace twice.
                }
                if (!markedRollback)
                {
                    log.debug("setting rollback only. Stacktrace:", new DebugStackTrace());
                }
            }
            super.setRollbackOnly();
        }

    }

    @Override
    @Unwrap
    public UserTransaction getTransaction() throws NamingException
    {
        return new LoggingUserTransaction(super.getTransaction());
    }
    
}
