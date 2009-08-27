package org.ccci.transaction;


import javax.transaction.Status;

public class TransactionStatuses
{

    //prevent instantiation
    private TransactionStatuses(){}
    

    /**
     * Useful for debugging, gives a string form for the given int flag corresponding to
     * a transaction status.  
     * @param status must be one of constants defined in {@link Status}
     * @return a string representation of the status (e.g. "ACTIVE", "MARKED_ROLLBACK", etc)
     * @throws IllegalStateException if the given flag is not a valid transaction status
     */
    public static String toString(int status)
    {
        switch (status)
        {
            case 0 : return "ACTIVE";
            case 1 : return "MARKED_ROLLBACK";
            case 2 : return "PREPARED";
            case 3 : return "COMMITTED";
            case 4 : return "ROLLEDBACK";
            case 5 : return "UNKNOWN";
            case 6 : return "NO_TRANSACTION";
            case 7 : return "PREPARING";
            case 8 : return "COMMITTING";
            case 9 : return "ROLLING_BACK";
            default:
                throw new IllegalArgumentException("Invalid transaction status flag: " + status);
        }
    }
}
