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

    public void workAndHandleExceptions()
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

}
