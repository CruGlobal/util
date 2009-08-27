package org.ccci.debug;

import junit.framework.Assert;

import org.ccci.Wrapper;
import org.junit.Test;

import com.google.common.collect.Iterables;

public class RecordedExceptionsTest
{

    @Test
    public void testNoneSwallowed()
    {
        RecordedExceptions recordedExceptions = new RecordedExceptions();

        RuntimeException runtimeException = new RuntimeException("bomb");
        recordedExceptions.recordThrownException(runtimeException);
        recordedExceptions.recordThrownException(runtimeException);
        
        Exception checkedException = new Exception("application");
        recordedExceptions.recordThrownException(checkedException);
        
        Assert.assertEquals(runtimeException,
            Iterables.getOnlyElement(recordedExceptions.getSystemExceptions().keySet()));
        Assert.assertEquals(runtimeException,
            Iterables.getOnlyElement(recordedExceptions.getUnhandledExceptions().keySet()));

        recordedExceptions.recordHandledException(runtimeException);
        
        Assert.assertEquals(runtimeException,
            Iterables.getOnlyElement(recordedExceptions.getSystemExceptions().keySet()));
        Assert.assertTrue(recordedExceptions.getUnhandledExceptions().isEmpty());
    }
    
    @Test
    public void testSwallowed()
    {
        RecordedExceptions recordedExceptions = new RecordedExceptions();
        final Wrapper<Boolean> wasLogged = new Wrapper<Boolean>(Boolean.FALSE);
        
        RuntimeException runtimeException = new RuntimeException("bomb");
        recordedExceptions.recordThrownException(runtimeException);
        recordedExceptions.recordThrownException(runtimeException);
        
        recordedExceptions.setLog(new EmptyLog(){
            @Override
            public void warn(Object object, Throwable t, Object... params)
            {
                //should only log once
                Assert.assertFalse(wasLogged.get());
                wasLogged.set(Boolean.TRUE);
            }
        });
        recordedExceptions.checkForSwallowedExceptions();
        Assert.assertTrue(wasLogged.get());
    }
}
