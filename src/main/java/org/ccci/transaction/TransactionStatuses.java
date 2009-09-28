package org.ccci.transaction;


import java.util.Map;

import javax.transaction.Status;

import com.google.common.collect.ImmutableMap;

public class TransactionStatuses
{

    //prevent instantiation
    private TransactionStatuses(){}
    
    private static final Map<Integer, String> STATUS_FLAG_TO_LABEL =
        ImmutableMap.<Integer, String>builder()
            .put(0, "ACTIVE")
            .put(1, "MARKED_ROLLBACK")
            .put(2, "PREPARED")
            .put(3, "COMMITTED")
            .put(4, "ROLLEDBACK")
            .put(5, "UNKNOWN")
            .put(6, "NO_TRANSACTION")
            .put(7, "PREPARING")
            .put(8, "COMMITTING")
            .put(9, "ROLLING_BACK")
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
}
