package org.ccci.debug;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.ccci.util.strings.Strings;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * @author Matt Drees
 *
 */
public class ExceptionReport implements Report
{

    private ExceptionEvent exceptionEvent;
    private HandledExceptionEvent handledExceptionEvent;
    private RequestEvent thisRequestEvent;
    private final List<RequestEvent> recentRequestEvents = Lists.newArrayList();
    private final List<String> otherExceptionDetails = Lists.newArrayList();
    private final List<LoggingEvent> loggingEvents = Lists.newArrayList();
    private Layout loggingLayout;
    private final Multimap<ExceptionLocation, ExceptionEvent> unexpectedExceptionsByLocation = LinkedHashMultimap.create();

    private final DateTimeFormatter formatter = ISODateTimeFormat.dateHourMinuteSecondMillis();

    
    public String getSummary()
    {
        return exceptionEvent.getRootThrowable().getClass().getName();
    }
    
    public String getReportAsString()
    {
        ReportStringBuilder builder = new ReportStringBuilder();

        builder.appendLine("Root Throwable", Strings.truncate(exceptionEvent.getRootThrowable().toString(), 300, "..."));
        builder.appendBreak();
        builder.appendLine("Location", exceptionEvent.getExceptionLocation().getLocation());
        builder.appendLine("Occurred At", formatter.print(exceptionEvent.getOccuredAt()));
        builder.appendLine("Occurred During JSF Phase", exceptionEvent.getPhaseId());
        builder.appendLine("Username", exceptionEvent.getUsername());
        builder.appendLine("Session ID", exceptionEvent.getSessionId());
        builder.appendLine("User Agent", exceptionEvent.getUserAgent());
        builder.appendLine("IP Address", exceptionEvent.getRemoteIPAddress());
        builder.appendBreak();
        for (Map.Entry<String, String> entry : exceptionEvent.getAdditionalContextInformation().entrySet())
        {
            builder.appendLine(entry.getKey(), entry.getValue());
        }
        builder.appendBreak();
        
        ReportHelper.addRequestInfo(thisRequestEvent, builder);
        builder.appendBreak();
        if (recentRequestEvents.size() > 1)
        {
            builder.appendBreak();
            builder.appendNote("Previous Request:");
            RequestEvent previousRequestEvent = recentRequestEvents.get(recentRequestEvents.size() - 1);
            builder.appendLine("Occured at", formatter.print(previousRequestEvent.getOccurredAt()));
            ReportHelper.addRequestInfo(previousRequestEvent, builder);
            builder.appendBreak();
        }
        
        builder.appendChunk("Reduced Stack Trace", ExceptionUtil.printStacktraceToString(exceptionEvent.getThrowable()));
        builder.appendBreak();

        if (!otherExceptionDetails.isEmpty())
        {
            builder.appendList("Other Exception Details", otherExceptionDetails);
        }
        if (handledExceptionEvent != null && handledExceptionEvent.getExceptionEvent() != exceptionEvent)
        {
            builder.appendList("Handled Exception", ExceptionUtil.summarizeException(handledExceptionEvent.getHandledThrowable()));
        }
        
        List<String> exceptionSummaryLines = Lists.newArrayList();
        for (ExceptionLocation exceptionLocation : unexpectedExceptionsByLocation.keySet())
        {
            Collection<ExceptionEvent> exceptionEvents = unexpectedExceptionsByLocation.get(exceptionLocation);
            if (exceptionEvents.size() > 1)
            {
                exceptionSummaryLines.add(String.format("%s at %s (%s occurances: first at %s, last at %s)",
                    exceptionLocation.getThrowableClass(),
                    exceptionLocation.getLocation(),
                    exceptionEvents.size(),
                    Collections.min(exceptionEvents).getOccuredAt(),
                    Collections.max(exceptionEvents).getOccuredAt()));
            }
            else if (exceptionEvents.size() == 1)
            {
                exceptionSummaryLines.add(String.format("%s at %s (at %s)",
                    exceptionLocation.getThrowableClass(),
                    exceptionLocation.getLocation(),
                    exceptionEvents.iterator().next().getOccuredAt()));
            }
            else 
            {
                throw new AssertionError("exceptionEvents should never be empty");
            }
        }
        if (exceptionSummaryLines.size() > 1)
        {
            builder.appendList("Exception Summary", exceptionSummaryLines);
        }
        builder.appendBreak();
        
        if (loggingLayout != null && !loggingEvents.isEmpty())
        builder.appendChunk("Log Events", ReportHelper.buildLogChunk(loggingLayout, loggingEvents));
        
        if (recentRequestEvents.size() > 2)
        {
            builder.appendBreak();
            builder.appendNote("More Previous Requests:");
            for (RequestEvent event : Iterables.skip(Lists.reverse(recentRequestEvents), 1))
            {
                builder.appendLine("Occured at", formatter.print(event.getOccurredAt()));
                ReportHelper.addRequestInfo(event, builder);
                builder.appendBreak();
            }
        }
        
        builder.appendBreak();
        List<String> httpHeaderLines = Lists.newArrayList();
        for (Entry<String, String> entry : thisRequestEvent.getHttpHeaders().entrySet())
        {
            httpHeaderLines.add(entry.getKey() + "=" + entry.getValue());
        }
    
        if (httpHeaderLines.isEmpty())
        {
            builder.appendNote("No Http Headers");
        }
        else
        {
            builder.appendList("Http Headers", httpHeaderLines);
        }
        
        return builder.toString();
    }

    public ExceptionEvent getExceptionEvent()
    {
        return exceptionEvent;
    }

    public void setExceptionEvent(ExceptionEvent exceptionEvent)
    {
        this.exceptionEvent = exceptionEvent;
    }

    public List<RequestEvent> getRecentRequestEvents()
    {
        return recentRequestEvents;
    }

    public List<String> getOtherExceptionDetails()
    {
        return otherExceptionDetails;
    }

    public HandledExceptionEvent getHandledExceptionEvent()
    {
        return handledExceptionEvent;
    }

    public void setHandledExceptionEvent(HandledExceptionEvent handledExceptionEvent)
    {
        this.handledExceptionEvent = handledExceptionEvent;
    }

    public List<LoggingEvent> getLoggingEvents()
    {
        return loggingEvents;
    }

    public Multimap<ExceptionLocation, ExceptionEvent> getUnexpectedExceptionsByLocation()
    {
        return unexpectedExceptionsByLocation;
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

}
