package org.ccci.servlet;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.ccci.util.reflect.Reflections;

public class KeepAliveCacheTimerCleaningContextListener implements ServletContextListener
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
            cleanKeepAliveCacheTimerThread();
        }
        catch(Exception e)
        {
            log.warn("exception while cleaning Keep-Alive-Timer thread; ignoring", e);
        }
    }


    @SuppressWarnings("restriction")
    private void cleanKeepAliveCacheTimerThread() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        sun.net.www.http.KeepAliveCache keepAliveCache = Reflections.getStaticFieldValue(sun.net.www.http.HttpClient.class, "kac");
        synchronized (keepAliveCache)
        {
            Thread keepAliveTimer = Reflections.getField(keepAliveCache, sun.net.www.http.KeepAliveCache.class, "keepAliveTimer");
            if (keepAliveTimer != null &&
                keepAliveTimer.getContextClassLoader() == getClass().getClassLoader())
            {
                keepAliveTimer.setContextClassLoader(sun.net.www.http.KeepAliveCache.class.getClassLoader());
            }
        }
    }

}
