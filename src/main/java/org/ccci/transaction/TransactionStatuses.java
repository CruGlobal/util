package org.ccci.transaction;


import java.util.Map;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.ccci.util.contract.Preconditions;

import com.google.common.collect.ImmutableMap;

public class TransactionStatuses
{

    //prevent instantiation
    private TransactionStatuses(){}
    
    private static final Map<Integer, String> STATUS_FLAG_TO_LABEL =
        ImmutableMap.<Integer, String>builder()
            //in ascending order (by int value):
            .put(Status.STATUS_ACTIVE, "ACTIVE")
            .put(Status.STATUS_MARKED_ROLLBACK, "MARKED_ROLLBACK")
            .put(Status.STATUS_PREPARED, "PREPARED")
            .put(Status.STATUS_COMMITTED, "COMMITTED")
            .put(Status.STATUS_ROLLEDBACK, "ROLLEDBACK")
            .put(Status.STATUS_UNKNOWN, "UNKNOWN")
            .put(Status.STATUS_NO_TRANSACTION, "NO_TRANSACTION")
            .put(Status.STATUS_PREPARING, "PREPARING")
            .put(Status.STATUS_COMMITTING, "COMMITTING")
            .put(Status.STATUS_ROLLING_BACK, "ROLLING_BACK")
            .build();
    
    
    /**
     * Useful for debugging, gives a string form for the given int flag corresponding to
     * a transaction status.  
     * @param status must be one of constants defined in {@link Status}
     * @return a string representation of the status (e.g. "ACTIVE", "MARKED_ROLLBACK", etc)
     * @throws IllegalStateException if the given flag is not a valid transaction status
     */
    public static String toString(int status)
    {
        String label = STATUS_FLAG_TO_LABEL.get(status);
        if (label == null)
        {
            throw new IllegalArgumentException("Invalid transaction status flag: " + status);
        }
        return label;
    }

    /**
     * Gets the transaction status as a String.  If {@link UserTransaction#getStatus()} throws an exception, it is not propagated,
     * so this method may be used in a <code>catch</code> block.
     * @param transaction
     * @return
     */
    public static Object getStatusAsString(UserTransaction transaction)
    {
        Preconditions.checkNotNull(transaction, "transaction is null");
        try
        {
            return toString(transaction.getStatus());
        }
        catch (Exception e)
        {
            return "<Not Available - UserTransaction.getStatus() threw exception>";
        }
    }
}
