package org.ccci.transaction;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.IOException;

import javax.naming.NamingException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.log.Log;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.transaction.UserTransaction;
import org.jboss.seam.web.AbstractFilter;

/**
 * Transactions *should* have been either committed or rolled back by the time the request ends and
 * control returns to this filter, but if not, then this filter will log an error message and 
 * roll back the transaction.
 * 
 * In addition, double check that when a request is starting there is no hanging transaction.  Rollback
 * if there is.
 * 
 * @author Matt Drees
 */
@Name("deadTransactionCheckingFilter")
@Scope(APPLICATION)
@org.jboss.seam.annotations.intercept.BypassInterceptors
@Filter(around = "recordedExceptionsFilter", within = "org.jboss.seam.web.ajax4jsfFilter")
public class DeadTransactionCheckingFilter extends AbstractFilter
{

    @Logger Log log;
    
    @Override
    public void doFilter(ServletRequest reqest, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException
    {
        try
        {
            verifyNoDeadTransaction();
            filterChain.doFilter(reqest, response);
        }
        finally
        {
            verifyNoDeadTransaction();
        }
    }
    
    private void verifyNoDeadTransaction()
    {
        boolean beganCall = false;
        if (!Contexts.isEventContextActive())
        {
            beganCall = true;
            Lifecycle.beginCall();
        }
        try 
        {
           UserTransaction transaction = retrieveUserTransaction();
           if ( !transaction.isNoTransaction())
           {
               log.error("request thread has a transaction with status #0",
                    TransactionStatuses.toString(transaction.getStatus()));
               rollback(transaction);
           }
        }
        catch (Exception te)
        {
            org.ccci.util.Exceptions.swallow(te, "could not check transaction status");
        }
        finally
        {
            if (beganCall) Lifecycle.endCall();
        }
    }

    private UserTransaction retrieveUserTransaction() throws NamingException
    {
        return Transaction.instance();
    }

    private void rollback(UserTransaction transaction)
    {
        log.error("killing transaction");
        try
        {
            transaction.rollback();
        }
        catch (Exception te)
        {
            org.ccci.util.Exceptions.swallow(te, "could not roll back transaction");
        }
    }
}
