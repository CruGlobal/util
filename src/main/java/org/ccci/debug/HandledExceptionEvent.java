package org.ccci.debug;

import java.io.Serializable;

import com.google.common.base.Preconditions;

public class HandledExceptionEvent implements Serializable, Comparable<HandledExceptionEvent>
{
    private static final long serialVersionUID = 1L;

    private final String handledBy;
    private final ExceptionEvent exceptionEvent;

    public HandledExceptionEvent(ExceptionEvent exceptionEvent, String handledBy)
    {
        Preconditions.checkNotNull(exceptionEvent, "exceptionEvent is null");
        Preconditions.checkNotNull(handledBy, "handledBy is null");

        this.handledBy = handledBy;
        this.exceptionEvent = exceptionEvent;
    }
    
    public Throwable getHandledThrowable()
    {
        return exceptionEvent.getThrowable();
    }
    
    public String getHandledBy()
    {
        return handledBy;
    }

    public ExceptionEvent getExceptionEvent()
    {
        return exceptionEvent;
    }

    public int compareTo(HandledExceptionEvent o)
    {
        Preconditions.checkNotNull(o, "other handledExceptionEvent is null");
        return exceptionEvent.getOccuredAt().compareTo(o.exceptionEvent.getOccuredAt());
    }
    
}

