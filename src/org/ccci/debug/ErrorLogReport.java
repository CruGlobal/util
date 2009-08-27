package org.ccci.debug;

import java.util.List;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.ccci.util.strings.Strings;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ErrorLogReport implements Report
{

    private RequestEvent thisRequestEvent;
    private final List<RequestEvent> recentRequestEvents = Lists.newArrayList();
    private final List<LoggingEvent> loggingEvents = Lists.newArrayList();
    private Layout loggingLayout;
    
    private String username;


    private final DateTimeFormatter formatter = ISODateTimeFormat.dateHourMinuteSecondMillis();
    
    public String getSummary()
    {
        return "Error logged: " + Strings.truncate(getFirstErrorMessage(), 30, "...");
    }
    
    private String getFirstErrorMessage()
    {
        for (LoggingEvent event : loggingEvents)
        {
            if (event.getLevel().toInt() >= Level.ERROR_INT)
            {
                return event.getRenderedMessage();
            }
        }
        return null;
    }

    
    //TODO: needs additionalContextInformation for conference id, just like ExceptionReport.  
    //Need to share more code between these somehow.
    public String getReportAsString()
    {
        ReportStringBuilder builder = new ReportStringBuilder();

        builder.appendLine("Error Message", getFirstErrorMessage());
        builder.appendBreak();
        ReportHelper.addRequestInfo(thisRequestEvent, builder);
        builder.appendBreak();
        
        builder.appendLine("Username", username);
        //TODO: need to move these useful items out of ExceptionEvent, so I can have them here
//        builder.appendLine("Session ID", exceptionEvent.getSessionId());
//        builder.appendLine("User Agent", exceptionEvent.getUserAgent());
//        builder.appendLine("IP Address", exceptionEvent.getRemoteIPAddress());
        builder.appendBreak();

        builder.appendBreak();
        List<RequestEvent> recentRequestEvents2 = this.recentRequestEvents;
        if (recentRequestEvents2.size() > 1)
        {
            ReportHelper.addRequestInfo(recentRequestEvents2.get(1), builder);
            builder.appendBreak();
        }
        
        if (loggingLayout != null && !loggingEvents.isEmpty())
        builder.appendChunk("Log Events", ReportHelper.buildLogChunk(loggingLayout, loggingEvents));
        
        if (recentRequestEvents.size() > 2)
        {
            builder.appendBreak();
            builder.appendNote("Previous Requests:");
            for (RequestEvent event : Iterables.skip(recentRequestEvents, 1))
            {
                builder.appendLine("Occured at", formatter.print(event.getOccuredAt()));
                ReportHelper.addRequestInfo(event, builder);
                builder.appendBreak();
            }
        }
        return builder.toString();
    }

    public List<RequestEvent> getRecentRequestEvents()
    {
        return recentRequestEvents;
    }


    public List<LoggingEvent> getLoggingEvents()
    {
        return loggingEvents;
    }

    public Layout getLoggingLayout()
    {
        return loggingLayout;
    }

    public void setLoggingLayout(Layout loggingLayout)
    {
        this.loggingLayout = loggingLayout;
    }

    public void setThisRequestEvent(RequestEvent thisRequestEvent)
    {
        this.thisRequestEvent = thisRequestEvent;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    
}
