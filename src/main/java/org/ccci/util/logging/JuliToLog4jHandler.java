package org.ccci.util.logging;

import java.text.MessageFormat;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * A JULI (java.util.logging) handler that redirects java.util.logging messages to Log4J
 * http://wiki.apache.org/myfaces/Trinidad_and_Common_Logging
 * 
 * initially copied from http://shrubbery.mynetgear.net/c/display/W/Routing+java.util.logging+messages+to+Log4J

 * @author Joshua Davis
 * @author Matt Drees
 */
public class JuliToLog4jHandler extends Handler
{

    public void publish(LogRecord record)
    {
        org.apache.log4j.Logger log4j = getTargetLogger(record.getLoggerName());
        Priority priority = toLog4j(record.getLevel());
        log4j.log(priority, toLog4jMessage(record), record.getThrown());
    }

    static Logger getTargetLogger(String loggerName)
    {
        return Logger.getLogger(loggerName);
    }

    public static Logger getTargetLogger(Class<?> clazz)
    {
        return getTargetLogger(clazz.getName());
    }

    private String toLog4jMessage(LogRecord record)
    {
        // Format message
        try
        {
            String message = record.getMessage();
            Object parameters[] = record.getParameters();
            if (parameters != null && parameters.length != 0)
            {
                // Check for the first few parameters ?
                if (message.indexOf("{0}") >= 0 ||
                    message.indexOf("{1}") >= 0 ||
                    message.indexOf("{2}") >= 0 ||
                    message.indexOf("{3}") >= 0)
                {
                    message = MessageFormat.format(message, parameters);
                }
            }
            return message;
        }
        catch (Exception ex)
        {
            return record.getMessage();
        }
    }

    private org.apache.log4j.Level toLog4j(Level level)
    {
        if (Level.OFF == level)
        {
            return org.apache.log4j.Level.OFF;
        }
        else if (level.intValue() > Level.SEVERE.intValue())
        {
            return org.apache.log4j.Level.FATAL;
        }
        if (inRange(level, Level.WARNING, Level.SEVERE))
        {
            return org.apache.log4j.Level.ERROR;
        }
        else if (inRange(level, Level.INFO, Level.WARNING))
        {
            return org.apache.log4j.Level.WARN;
        }
        else if (inRange(level, Level.CONFIG, Level.INFO))
        {
            return org.apache.log4j.Level.INFO;
        }
        else if (inRange(level, Level.FINER, Level.CONFIG))
        {
            return org.apache.log4j.Level.DEBUG;
        }
        else if (level.intValue() <= Level.FINER.intValue())
        {
            return org.apache.log4j.Level.TRACE;
        }
        else 
        {
            throw new AssertionError();
        }
    }

    private boolean inRange(Level level, Level lowerBoundExclusive, Level upperBoundInclusive)
    {
        return level.intValue() > lowerBoundExclusive.intValue() &&
            level.intValue() <= upperBoundInclusive.intValue();
    }

    @Override
    public void flush()
    {
        // nothing to do
    }

    @Override
    public void close()
    {
        // nothing to do
    }
}
