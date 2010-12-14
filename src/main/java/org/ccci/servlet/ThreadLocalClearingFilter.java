package org.ccci.servlet;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;
import org.ccci.util.reflect.Reflections;

import com.google.common.collect.Sets;

public class ThreadLocalClearingFilter implements Filter
{

    Logger log = Logger.getLogger(this.getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    @Override
    public void destroy()
    {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException
    {
        Set<ThreadLocal<?>> existingThreadLocals = null;
        try
        {
            existingThreadLocals = getExistingThreadLocals();
        }
        catch (Exception e)
        {
            log.error("exception getting thread locals:", e);
        }
        chain.doFilter(request, response);
        try
        {
            clearThreadLocals(existingThreadLocals);
        }
        catch (Exception e)
        {
            log.error("exception clearing thread locals:", e);
        }
        
    }

    private Set<ThreadLocal<?>> getExistingThreadLocals() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        Set<ThreadLocal<?>> existingThreadLocals = Sets.newHashSet();
        
        Object table = getThreadLocalMapTable();
        int threadLocalCount = Array.getLength(table);

        for (int i=0; i < threadLocalCount; i++) {
            Object entry = Array.get(table, i);
            if (entry != null) {
                ThreadLocal<?> key = Reflections.invokeMethod(entry, Reference.class, "get");
                if (key != null) 
                {
                    existingThreadLocals.add(key);
                } 
            }
        }
        return existingThreadLocals;
    }

    private void clearThreadLocals(Set<ThreadLocal<?>> existingThreadLocals) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        Object table = getThreadLocalMapTable();
        int threadLocalCount = Array.getLength(table);
        StringBuilder cleanedThreadLocalValues = new StringBuilder();

        int leakCount = 0;

        for (int i=0; i < threadLocalCount; i++) {
            Object entry = Array.get(table, i);
            if (entry != null) {
                ThreadLocal<?> key = Reflections.invokeMethod(entry, Reference.class, "get");
                if (key != null && !existingThreadLocals.contains(key))
                {
                    Object value = Reflections.getField(entry, entry.getClass(), "value");
                    
                    if (value != null) 
                    {
                        ClassLoader webappClassLoader = Thread.currentThread().getContextClassLoader();
                        if (value.getClass().getClassLoader() == webappClassLoader)
                        {
                            cleanedThreadLocalValues.append(value).append(", ");
                            Reflections.setField(entry, entry.getClass(), "value", null);
                            leakCount++;
                        }
                    }
                    else 
                    {
                        cleanedThreadLocalValues.append("null, ");
                    }
                }
            }
        }

        if ( leakCount > 0)
        {
            StringBuilder message = new StringBuilder();
            message.append("possible ThreadLocal leaks: ")
                    .append(leakCount)
                    .append(" of ")
                    .append(threadLocalCount)
                    .append(" = [")
                    .append(cleanedThreadLocalValues.substring(0, cleanedThreadLocalValues.length() - 2))
                    .append("] ");
            log.warn(message);
            log.warn("leaked thread locals have been forcefully removed");
        }

    }

    private Object getThreadLocalMapTable() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException
    {
        Thread thread = Thread.currentThread();

        Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
        threadLocalsField.setAccessible(true);

        Class<?> threadLocalMapKlazz = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
        Field tableField = threadLocalMapKlazz.getDeclaredField("table");
        tableField.setAccessible(true);

        Object table = tableField.get(threadLocalsField.get(thread));
        return table;
    }

}
