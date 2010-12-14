package org.ccci.servlet;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ThreadLocalClearingFilterTest
{

    ThreadLocalClearingFilter filter;

    @Mock
    FilterChain chain;

    @Mock
    ServletRequest request;

    @Mock
    ServletResponse response;
    
    Logger log;

    @BeforeClass
    public void setupLogging()
    {
        BasicConfigurator.configure();
    }
    
    @BeforeMethod
    public void setup()
    {
        MockitoAnnotations.initMocks(this);

        filter = new ThreadLocalClearingFilter();
        log = spy(Logger.getLogger(ThreadLocalClearingFilterTest.class));
        filter.log = log; 
    }
    
    @Test
    public void existingThreadLocalDoesNotTriggerWarning() throws IOException, ServletException
    {
        ThreadLocal<String> outterThreadLocal = new ThreadLocal<String>();
        outterThreadLocal.set("foo");
        filter.doFilter(request, response, chain);
        Assert.assertEquals(outterThreadLocal.get(), "foo");
        verifyNoMoreInteractions(log);
    }
    
    static class Bar {}
    
    @Test
    public void leakedThreadLocalTriggersWarning() throws IOException, ServletException
    {
        doAnswer(new Answer<Void>(){

            public Void answer(InvocationOnMock invocation) throws Throwable
            {
                ThreadLocal<Bar> leakedThreadLocal = new ThreadLocal<Bar>();
                leakedThreadLocal.set(new Bar());
                return null;
            }
            
        }).when(chain).doFilter(request, response);
        filter.doFilter(request, response, chain);
        
        verify(log, times(2)).warn(any());
    }
    
    
    @Test
    public void innerThreadLocalDoesNotTriggerWarning() throws IOException, ServletException
    {
        doAnswer(new Answer<Void>(){
            
            public Void answer(InvocationOnMock invocation) throws Throwable
            {
                ThreadLocal<String> leakedThreadLocal = new ThreadLocal<String>();
                leakedThreadLocal.set("bar");
                leakedThreadLocal.set(null);
                return null;
            }
            
        }).when(chain).doFilter(request, response);
        filter.doFilter(request, response, chain);
        
        verifyNoMoreInteractions(log);
    }
    
    
    
}
