package org.ccci.faces.lifecycle;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

import org.ccci.debug.RecordedExceptions;

import com.google.common.base.Preconditions;

public class ExceptionRecordingLifecycle extends javax.faces.lifecycle.Lifecycle
{

    private final Lifecycle delegate;
    
    public ExceptionRecordingLifecycle(Lifecycle lifecycle)
    {
        delegate = lifecycle;
    }

    @Override
    public void addPhaseListener(PhaseListener listener)
    {
        delegate.addPhaseListener(new ExceptionRecordingPaseListenerWrapper(listener));
    }

    @Override
    public void execute(FacesContext context) throws FacesException
    {
        try
        {
            delegate.execute(context);
        }
        catch (FacesException e)
        {
            recordException(e);
            throw e;
        }
        catch (RuntimeException e)
        {
            recordException(e);
            throw e;
        }
    }

    private void recordException(Exception e)
    {
        RecordedExceptions recordedExceptions = RecordedExceptions.instance();
        if (recordedExceptions != null)
        {
            recordedExceptions.recordThrownException(e);
        }
    }

    
    @Override
    public PhaseListener[] getPhaseListeners()
    {
        return delegate.getPhaseListeners();
    }

    @Override
    public void removePhaseListener(PhaseListener listener)
    {
        delegate.removePhaseListener(new ExceptionRecordingPaseListenerWrapper(listener));
    }

    @Override
    public void render(FacesContext context) throws FacesException
    {
        delegate.render(context);
    }
    
    
    class ExceptionRecordingPaseListenerWrapper implements PhaseListener
    {

        private static final long serialVersionUID = 1L;
        
        private final PhaseListener delegate;
        
        public ExceptionRecordingPaseListenerWrapper(PhaseListener delegate)
        {
            this.delegate = Preconditions.checkNotNull(delegate);
        }

        @Override
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

        @Override
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

        @Override
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
            return "ExceptionRecordingLifecycle[" + delegate.toString() + "]";
        }
    }

}
