package org.ccci.seam.transaction;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import org.ccci.transaction.TransactionStatuses;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jboss.seam.transaction.Transaction;

@Name("org.ccci.transactionEventLogger")
@Scope(APPLICATION)
@Install(precedence = BUILT_IN)
@BypassInterceptors
public class TransactionEventLogger
{

    @Logger
    Log log;

    @Observer(Transaction.TRANSACTION_FAILED)
    public void addTransactionFailedMessage(int status)
    {
        log.info("transaction failed during request. Status: {0}", TransactionStatuses.toString(status));
    }

}
