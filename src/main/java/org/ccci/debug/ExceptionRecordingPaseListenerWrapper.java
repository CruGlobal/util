/**
 * 
 */
package org.ccci.debug;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import com.google.common.base.Objects;

class ExceptionRecordingPaseListenerWrapper implements PhaseListener
{

    private static final long serialVersionUID = 1L;
    
    private final PhaseListener delegate;
    
    public ExceptionRecordingPaseListenerWrapper(PhaseListener delegate)
    {
        this.delegate = Objects.nonNull(delegate);
    }

    public void afterPhase(PhaseEvent event)
    {
        try
        {
            delegate.afterPhase(event);
        }
        catch (RuntimeException e)
        {
            recordException(e);
            throw e;
        }
    }

    public void beforePhase(PhaseEvent event)
    {
        try
        {
            delegate.beforePhase(event);
        }
        catch (RuntimeException e)
        {
            recordException(e);
            throw e;
        }
    }

    private static void recordException(Exception e)
    {
        ExceptionContext.getCurrentInstance().recordThrownException(e);
    }

    public PhaseId getPhaseId()
    {
        return delegate.getPhaseId();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != this.getClass()) return false;
        return delegate.equals(((ExceptionRecordingPaseListenerWrapper) obj).delegate);
    }
    
    @Override
    public int hashCode()
    {
        return delegate.hashCode();
    }
    
    @Override
    public String toString()
    {
        return "ExceptionRecordingPaseListenerWrapper[" + delegate.toString() + "]";
    }
}