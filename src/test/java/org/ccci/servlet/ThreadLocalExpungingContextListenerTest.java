package org.ccci.servlet;

import org.testng.annotations.Test;

public class ThreadLocalExpungingContextListenerTest
{

    @Test
    public void testExpungeThreadLocalValues()
    {
        ThreadLocalExpungingContextListener listener = new ThreadLocalExpungingContextListener();
        
        listener.contextDestroyed(null);
    }
}
