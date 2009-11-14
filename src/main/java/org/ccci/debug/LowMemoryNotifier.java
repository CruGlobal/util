package org.ccci.debug;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

@Name("lowMemoryNotifier")
@Scope(APPLICATION)
@BypassInterceptors
@Startup
public class LowMemoryNotifier implements MemoryWarningSystem.Listener
{
    private final Log log = Logging.getLog(LowMemoryNotifier.class);
    
    private final MemoryWarningSystem memoryWarningSystem = new MemoryWarningSystem();

    private final LowMemoryEventProcessor processor = new LowMemoryEventProcessor();
    
    @Create
    public void onCreate()
    {
        MemoryWarningSystem.setPercentageUsageThreshold(0.90);
        processor.startup();
        memoryWarningSystem.startup(this);
    }
    
    @Destroy
    public void onDestroy()
    {
        try
        {
            memoryWarningSystem.shutdown();
        }
        finally
        {
            processor.shutdown();
        }
    }
    
    
    /**
     * @see MemoryWarningSystem.Listener#memoryUsageLow(long, long)
     * 
     * The thread that calls this method can't send emails for some reason, I haven't nailed down yet why.
     * Something to do with the classpath not being quite right, with activation.jar and mail.jar.  I get
     * something like javax.activation.UnsupportedDataTypeException: no object DCH for MIME type xxx/xxxx javax.mail.MessagingException: IOException .
     * see http://www.jguru.com/faq/view.jsp?EID=237257 for some discussion on this.
     * 
     * So, as a workaround, make the report, but let a different thread (started by the webapp) handle the
     * mailing.
     */
    public void memoryUsageLow(long usedMemory, long maxMemory)
    {
        String summary = String.format("memory is very low");
        log.warn(summary);
        String report = createReport(usedMemory, maxMemory);
        processor.enqueueReport(new LowMemoryReport(summary, report));
    }

    private String createReport(long usedMemory, long maxMemory)
    {
        StringWriter writer = new StringWriter();
        PrintWriter reportWriter = new PrintWriter(writer);
        printMemory(usedMemory, maxMemory, reportWriter);
        printServer(reportWriter);
        printStacktraces(reportWriter);
        String report = writer.toString();
        return report;
    }

    private void printStacktraces(PrintWriter reportWriter)
    {
        reportWriter.println("Stacktraces for all threads:");
        reportWriter.println();
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> stackTraceEntry : allStackTraces.entrySet())
        {
            reportWriter.println(stackTraceEntry.getKey());
            StackTraceElement[] trace = stackTraceEntry.getValue();
            for (int i=0; i < trace.length; i++)
                reportWriter.println("\tat " + trace[i]);

            reportWriter.println();
        }
    }

    private void printServer(PrintWriter reportWriter)
    {
        reportWriter.print("server: ");
        try
        {
            reportWriter.println(InetAddress.getLocalHost().getHostName());
        }
        catch (UnknownHostException e)
        {
            reportWriter.println("unavailable due to UnknownHostException");
        }
    }

    private void printMemory(long usedMemory, long maxMemory, PrintWriter reportWriter)
    {
        reportWriter.print("used memory: ");
        reportWriter.println(usedMemory);
        reportWriter.print("max memory: ");
        reportWriter.println(maxMemory);
    }
    
    public static class LowMemoryReport implements Report
    {
        private final String summary;
        private final String report;
        
        public LowMemoryReport(String summary, String report)
        {
            this.summary = summary;
            this.report = report;
        }

        public String getSummary()
        {
            return summary;
        }

        public String getReportAsString()
        {
            return report;
        }
    }
}
