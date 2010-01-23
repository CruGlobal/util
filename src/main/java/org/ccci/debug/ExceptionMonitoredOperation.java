package org.ccci.debug;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.joda.time.DateTime;

/**
 * Useful for performing operations in non-request threads.
 * 
 * Now, only used by quartz-scheduled operations.
 * 
 * @author Matt Drees
 *
 */
public abstract class ExceptionMonitoredOperation
{
    Log log = Logging.getLog(ExceptionMonitoredOperation.class);
    
    protected abstract void work() throws Exception;

    public void workAndLogExceptions()
    {
        try
        {
            work();
        }
        catch (Exception e)
        {
            log.error("unhandled exception", e);
            InetAddress serverAddress;
            try
            {
                serverAddress = InetAddress.getLocalHost();
            } 
            catch (UnknownHostException uhe)
            {
                throw org.ccci.util.Exceptions.wrap(uhe);
            }

            ExceptionReporter.instance().sendReport(new SimpleReport(e, new DateTime(), serverAddress));
        }
    }
    

    public void workAndThrowExceptions() throws Exception
    {
        try
        {
            work();
        }
        catch (Exception workException)
        {
            try
            {
                InetAddress serverAddress;
                serverAddress = InetAddress.getLocalHost();
                ExceptionReporter.instance().sendReport(new SimpleReport(workException, new DateTime(), serverAddress));
            }
            catch (Exception reportException)
            {
                log.error("exception while trying to report exception; swallowing", reportException);
            }
            catch (Error error)
            {
                log.error("error while trying to report exception.  Swallowing original exception:", workException);
                throw error;
            }
            throw workException;
        }
    }

}
