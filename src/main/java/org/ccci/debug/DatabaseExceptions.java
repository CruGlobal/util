package org.ccci.debug;

import static org.jboss.seam.ScopeType.STATELESS;

import java.sql.SQLException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Useful logic for database exceptions.  Most of the logic & messages were migrated from the old staff services application, and
 * so some of it may not be correct or useful anymore.  Perhaps some pruning is in order.
 * 
 * @author Matt Drees
 */
@Name("databaseExceptions")
@Scope(STATELESS)
public class DatabaseExceptions 
{

    /**
     * Generate an appropriate message for the user for the given {@link SQLException}
     * @param sqlException
     * @return
     */
    public String getMessage(SQLException sqlException)
    {
        if (sqlException == null)
        {
            return null;
        }
        String exceptionMessage = sqlException.getMessage();
        if(isOracleDown(exceptionMessage))
        {
            return "The system is currently unavailable due either to maintenance or unexpected downtime. Please try again later.";
        }
        else if(isOracleQueryTimeout(exceptionMessage))
        {
            return "We are currently experiencing performance problems with our database.  Please try your request again, or come back later.";
        }
        else
        {
            return "Internal System Error";
        }
    }

    private boolean isOracleQueryTimeout(String exceptionMessage)
    {
        return exceptionMessage.startsWith("ORA-01013"); //user requested cancel
    }

    private boolean isOracleDown(String exceptionMessage)
    {
        return 
            exceptionMessage.startsWith("Io exception: Connection refused") ||
            exceptionMessage.startsWith("Io exception: The Network Adapter could not establish") ||
            exceptionMessage.startsWith("ORA-01089") || //immediate shutdown in progress
            exceptionMessage.startsWith("ORA-01034") || //ORACLE not available
            exceptionMessage.startsWith("ORA-00600") || //internal error code
            exceptionMessage.startsWith("ORA-01033"); //ORACLE initialization
    }
    
    /**
     * Whether or not the user should be asked to contact desktop services
     * @param e
     * @return
     */
    public boolean shouldDesktopServicesBeContacted(SQLException e)
    {
        return !isOracleDown(e.getMessage()) && !isOracleQueryTimeout(e.getMessage());
    }
    
}
