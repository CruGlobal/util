package org.ccci.debug;

import java.util.Iterator;

import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;


public class ExceptionRecordingLifecycleFactory extends LifecycleFactory
{

    private final LifecycleFactory delegate;

    public ExceptionRecordingLifecycleFactory(LifecycleFactory delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public void addLifecycle(String lifecycleId, Lifecycle lifecycle)
    {
        delegate.addLifecycle(lifecycleId, lifecycle);
    }

    @Override
    public Lifecycle getLifecycle(String lifecycleId)
    {
        return new ExceptionRecordingLifecycle(delegate.getLifecycle(lifecycleId));
    }

    @Override
    public Iterator<String> getLifecycleIds()
    {
        return delegate.getLifecycleIds();
    }

}
