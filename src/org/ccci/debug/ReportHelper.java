package org.ccci.debug;

import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import com.google.common.collect.Lists;

public class ReportHelper
{

    public static void addRequestInfo(RequestEvent requestEvent, ReportStringBuilder builder)
    {
        builder.appendLine("Server", "%s (%s)", requestEvent.getServerHost(), requestEvent.getServerIpAddress());
        builder.appendLine("Method", requestEvent.getMethod());
        builder.appendLine("URL", requestEvent.getRequestedUrl());
    
        List<String> queryParameterLines = Lists.newArrayList();
        for (Entry<String, String> entry : requestEvent.getQueryParameters().entries())
        {
            queryParameterLines.add(entry.getKey() + "=" + sanitize(entry));
        }
    
        if (queryParameterLines.isEmpty())
        {
            builder.appendNote("No Query Parameters");
        }
        else
        {
            builder.appendList("Query Parameters", queryParameterLines);
        }
    
        List<String> postParameterLines = Lists.newArrayList();
        for (Entry<String, String> entry : requestEvent.getPostParameters().entries())
        {
            postParameterLines.add(entry.getKey() + "=" + sanitize(entry));
        }
    
        if (postParameterLines.isEmpty())
        {
            builder.appendNote("No Post Parameters");
        }
        else
        {
            builder.appendList("Post Parameters", postParameterLines);
        }
    
        String referrer = requestEvent.getReferrer();
        if (referrer == null)
        {
            builder.appendNote("No Referer");
        }
        else
        {
            builder.appendLine("Referer", referrer);
        }
    }

    private static String sanitize(Entry<String, String> entry)
    {
        return entry.getKey().endsWith("cardNumber") ? "****************" : entry.getValue();
    }

    public static String buildLogChunk(Layout loggingLayout, List<LoggingEvent> loggingEvents)
    {
        StringBuilder loggingBuilder = new StringBuilder();
        String header = loggingLayout.getHeader();
    
        if (header != null)
        {
            loggingBuilder.append(header);
        }
    
        for (LoggingEvent event : loggingEvents)
        {
            loggingBuilder.append(loggingLayout.format(event));
    
            if (loggingLayout.ignoresThrowable())
            {
                String[] throwableStringRepresentation = event.getThrowableStrRep();
    
                if (throwableStringRepresentation != null)
                {
                    for (String line : throwableStringRepresentation)
                    {
                        loggingBuilder.append(line);
                        loggingBuilder.append(Layout.LINE_SEP);
                    }
                }
            }
        }
    
        String footer = loggingLayout.getFooter();
    
        if (footer != null)
        {
            loggingBuilder.append(footer);
        }
        return loggingBuilder.toString();
    }

}
