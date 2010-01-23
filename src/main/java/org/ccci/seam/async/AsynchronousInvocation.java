package org.ccci.seam.async;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.ccci.debug.ExceptionMonitoredOperation;

/**
 * Add exception handling to Scheduled Async operations
 * 
 * @author Matt Drees
 */
public class AsynchronousInvocation extends org.jboss.seam.async.AsynchronousInvocation
{

    //copies of superclass data; only for better toString() impl.  Grr.
    private String methodName;
    private Object[] args;
    private String componentName;
    
    public AsynchronousInvocation(Method method, String componentName, Object[] args)
    {
        super(method, componentName, args);

        this.methodName = method.getName();
        this.args = args==null ? new Object[0] : args;
        this.componentName = componentName;    
    }

    

    @Override
    public String toString()
    {
       return "AsynchronousInvocation(" + componentName + '.' + methodName + "(" + Arrays.toString(args) + "))";
    }

    
    @Override
	public void execute(final Object timer)
    {
        new ExceptionMonitoredOperation()
        {
            @Override
            protected void work()
            {
                AsynchronousInvocation.super.execute(timer);
            }
        }.workAndLogExceptions();
    }
    

    public void executeAndRethrowExceptions(final Object timer) throws Exception
    {
        new ExceptionMonitoredOperation()
        {
            @Override
            protected void work()
            {
                AsynchronousInvocation.super.execute(timer);
            }
        }.workAndThrowExceptions();
    }
    
    
    /*
     * This is messy, but it allows exception handling code to work within active Seam contexts
     */
    public void recover(final AsyncRecoveryAction asyncRecoveryAction)
    {

        new ContextualAsynchronousRequest(null)
        {
           
           @Override
           protected void process()
           {
              asyncRecoveryAction.recover();
           }
           
        }.run();
    }
    
    public static abstract class AsyncRecoveryAction
    {
        public abstract void recover();
    }

    private static final long serialVersionUID = 1L;

}
