package org.ccci.debug;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;



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

    private static void recordException(Exception e)
    {
        ExceptionContext.getCurrentInstance().recordThrownException(e);
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

}
