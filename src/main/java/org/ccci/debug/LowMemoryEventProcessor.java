package org.ccci.debug;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.ccci.debug.LowMemoryNotifier.LowMemoryReport;
import org.ccci.util.Exceptions;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.google.common.base.Preconditions;

public class LowMemoryEventProcessor implements Runnable
{
    private Log log = Logging.getLog(LowMemoryEventProcessor.class);
    
    BlockingQueue<LowMemoryReport> reportQueue = new LinkedBlockingQueue<LowMemoryReport>();
    
    // need volatile: http://www.ibm.com/developerworks/java/library/j-jtp06197.html#2.1 
    private volatile boolean continueRunning = true;

    private volatile Thread processorThread;

    public void run()
    {
        while (continueRunning)
        {
            try
            {
                LowMemoryReport report = reportQueue.poll(3, TimeUnit.SECONDS);
                if (report != null)
                {
                    log.debug("sending low memory report");
                    ExceptionReporter.instance().sendReport(report);
                }
            }
            catch (InterruptedException e)
            {
                //probably shutdown
            }
        }
    }
    
    public void enqueueReport(LowMemoryReport lowMemoryReport)
    {
        try
        {
            reportQueue.put(lowMemoryReport);
        }
        catch (InterruptedException e)
        {
            throw Exceptions.wrap(e);
        }
    }
    
    public void startup()
    {
        Preconditions.checkState(processorThread == null, "already started!");
        processorThread = new Thread(this, "low memory event processor");
        processorThread.setDaemon(true);
        processorThread.start();
    }

    public void shutdown()
    {
        continueRunning = false;
        processorThread.interrupt();
    }
}