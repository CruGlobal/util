package org.ccci.debug;


import java.net.InetAddress;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class SimpleReport implements Report
{

    private final Exception exception;
    private final DateTime occuredAt;
    private final InetAddress serverAddress;
    private final DateTimeFormatter formatter = ISODateTimeFormat.dateHourMinuteSecondMillis();
    
    public SimpleReport(Exception exception, DateTime occuredAt, InetAddress serverAddress)
    {
        this.exception = exception;
        this.occuredAt = occuredAt;
        this.serverAddress = serverAddress;
    }

    @Override
    public String getReportAsString()
    {
        ReportStringBuilder builder = new ReportStringBuilder();
        builder.appendLine("occured at", formatter.print(occuredAt));
        builder.appendBreak();

        builder.appendLine("Server", "%s (%s)", serverAddress.getHostName(), serverAddress.getHostAddress());
        builder.appendChunk("Stack Trace", ExceptionUtil.printStacktraceToString(exception));
        return builder.toString();
    }

    @Override
    public String getSummary()
    {
        return exception.getClass().getName();
    }

}
