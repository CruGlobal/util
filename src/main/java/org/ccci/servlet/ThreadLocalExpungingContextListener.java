package org.ccci.servlet;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.ccci.util.reflect.Reflections;

/**
 * 
 * todo: document this
 * 
 * somewhat inspired by http://weblogs.java.net/blog/jjviana/archive/2010/06/09/dealing-glassfish-301-memory-leak-or-threadlocal-thread-pool-bad-ide
 * 
 * @author Matt Drees
 */
public class ThreadLocalExpungingContextListener implements ServletContextListener
{
    
    Logger log = Logger.getLogger(this.getClass());

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
    }
    

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        try
        {
            cleanThreadLocals();
        }
        catch(Exception e)
        {
            log.warn("exception while cleaning thread locals; ignoring", e);
        }
    }


    private void cleanThreadLocals() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        log.debug("expunging all stale thread local map entries:");
        Thread[] threads = new Thread[256];
        Thread.enumerate(threads);

        for (int i = 0; i < threads.length; i++) {
            if (threads[i] != null) {
                cleanThreadLocals(threads[i]);
            }
        }

    }
    
    private void cleanThreadLocals(Thread thread) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException 
    {
        log.trace("expunging " + thread.getName() + "'s stale thread local map entries");
        Object threadLocalMap = Reflections.getField(thread, Thread.class, "threadLocals");
        if (threadLocalMap == null) {
            return;
        }

        Class<?> threadLocalMapClazz = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
        Reflections.invokeMethod(threadLocalMap, threadLocalMapClazz, "expungeStaleEntries");
    }



}
