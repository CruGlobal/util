package org.ccci.seam.async;

import java.lang.reflect.Method;

import org.ccci.debug.ExceptionMonitoredOperation;
import org.ccci.mojarra.MojarraELOperation;

/**
 * Add exception handling to Scheduled Async operations
 * 
 * Also includes mojarra workaround ( see {@link MojarraELOperation} )
 * 
 * @author Matt Drees
 */
public class AsynchronousInvocation extends org.jboss.seam.async.AsynchronousInvocation
{

    private static final long serialVersionUID = 1L;

    public AsynchronousInvocation(Method method, String componentName, Object[] args)
    {
        super(method, componentName, args);
    }

    @Override
	public void execute(final Object timer)
    {
        new ExceptionMonitoredOperation()
        {
            @Override
            protected void work() throws Exception
            {
                new MojarraELOperation()
                {
                    @Override
                    protected void work()
                    {
                        AsynchronousInvocation.super.execute(timer);
                    }
                }.workEnsuringMojarraELAvailable();
            }
        }.workAndHandleExceptions();
    }

}
