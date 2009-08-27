package org.ccci.debug;

import org.ccci.util.Exceptions;
import org.jboss.seam.annotations.intercept.AroundInvoke;
import org.jboss.seam.intercept.AbstractInterceptor;
import org.jboss.seam.intercept.InvocationContext;

public class ExceptionRecordingInterceptor extends AbstractInterceptor
{
    @AroundInvoke
    public Object aroundInvoke(InvocationContext invocation) throws Exception
    {
        try
        {
            return invocation.proceed();
        }
        catch (Exception e)
        {
            try
            {
                if (ExceptionContext.isExceptionContextAvailable())
                {
                    ExceptionContext.getCurrentInstance().recordThrownException(e);
                }
            }
            catch (Exception swallowed)
            {
                Exceptions.swallow(swallowed, "cannot report %s to ExceptionContext", e);
            }
            throw e;
        }
    }

    private static final long serialVersionUID = 1L;
}
