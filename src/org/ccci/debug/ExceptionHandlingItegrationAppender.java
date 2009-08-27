package org.ccci.debug;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class ExceptionHandlingItegrationAppender extends AppenderSkeleton implements Appender
{

    public ExceptionHandlingItegrationAppender()
    {
        super(true); //meaning, this appender is ready for use upon construction
    }
    
    @Override
    protected void append(LoggingEvent event)
    {
        if (this.layout == null) return; 
        if (!ExceptionContext.isExceptionContextAvailable()) return;
        
        event.getThreadName();
        event.getNDC();
        ExceptionContext.getCurrentInstance().recordLoggingEvent(event);
        ExceptionContext.getCurrentInstance().setLoggingLayout(layout);

    }

    @Override
    public void close()
    {
        this.closed = true;
    }

    @Override
    public boolean requiresLayout()
    {
        return true;
    }

    
    
}
