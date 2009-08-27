package org.ccci.debug;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.ccci.util.seam.Components;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.intercept.RootInterceptor;
import org.jboss.seam.log.Log;
import org.jboss.seam.servlet.SeamFilter;

@Name("stacktraceReducer")
@Scope(ScopeType.STATELESS)
@BypassInterceptors
@AutoCreate
public class StacktraceReducer implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Logger Log log;
    
    /**
     * Does two things.
     * 1. Removes the (potentially very large) bottom of the stack trace, which is mostly internal to the appserver
     * and which the developer rarely cares about.  Everything below SeamFilter is chopped off.
     * 
     * 2. Removes the 10ish or so stack frames that show interceptor & invocationContext method calls.  These
     * occur before any method call into a Seam component with enabled interception (All seam components by default have
     * interception enabled).
     * 
     * @param throwable
     */
    public void replaceStackTraceWithReducedStackTrace(Throwable throwable)
    {
        for (Throwable cause : ExceptionUtil.eachThrowableInChain(throwable)) 
        {
            List<StackTraceElement> stackTrace = new LinkedList<StackTraceElement>(Arrays.asList(cause.getStackTrace()));
            replaceApplicationServerStackFrames(stackTrace);
            replaceInterceptorStackFrames(stackTrace);
            cause.setStackTrace(stackTrace.toArray(new StackTraceElement[stackTrace.size()]));
        }
    }


    private void replaceInterceptorStackFrames(List<StackTraceElement> stackTrace)
    {
        boolean more = true;
        int bottom = stackTrace.size();
        while (more) {
            int interceptorBottom = getInterceptorInvokeLocation(stackTrace, bottom);
            if (interceptorBottom == -1) {
                more = false;
            } else {
                int interceptorTop = getReflectionInvokeLocation(stackTrace, interceptorBottom);
                if (interceptorTop == -1) {
                    more = false;
                } else {
                    int numberOfRemoved = interceptorBottom - interceptorTop;
                    StackTraceElement newElement = new StackTraceElement(
                            "...(" + numberOfRemoved + " interceptor frames removed)", "", "", 0);
                    replace(stackTrace, interceptorTop, interceptorBottom, newElement);
                }
                bottom = interceptorTop;
            }
        }
    }

    private void replaceApplicationServerStackFrames(List<StackTraceElement> stackTrace)
    {
        int seamFilterEndLocation = 0;
        seamFilterEndLocation = getSeamFilterEndLocation(stackTrace);
        if (seamFilterEndLocation != -1) {
            int numberOfRemoved = stackTrace.size() - seamFilterEndLocation;
            StackTraceElement newElement = new StackTraceElement(
                    "...(" + numberOfRemoved + " filter and app server stack frames removed)", "", "", 0);
            replace(stackTrace, seamFilterEndLocation, stackTrace.size(), newElement);
        }
    }

    private void replace(List<StackTraceElement> stackTrace, int top, int bottom, StackTraceElement newElement) {
        stackTrace.subList(top, bottom).clear();
        stackTrace.add(top, newElement);
    }
    
    private int getReflectionInvokeLocation(List<StackTraceElement> stackTrace, int bottom) {
        return findClassInStacktrace(stackTrace, bottom, Method.class, "invoke");
    }

    private int getInterceptorInvokeLocation(List<StackTraceElement> stackTrace, int bottom) {
        return findClassInStacktrace(stackTrace, bottom, RootInterceptor.class, "invoke");
    }
    
    private int findClassInStacktrace(List<StackTraceElement> stackTrace, int bottom, Class<?> searchClass, String method) {
        for (int i = bottom - 1; i >= 0; i--) {
            StackTraceElement element = stackTrace.get(i);
            if (element.getMethodName().equals(method)) {
                try {
                    Class<?> interceptorClass = Class.forName(element.getClassName());
                    if (searchClass.isAssignableFrom(interceptorClass)) {
                        return i;
                    }
                } catch (ClassNotFoundException cnf) {
                    log.debug("class not found: " + element.getClassName());
                } catch (Exception other) {
                    log.info("exception while trying to find class " + element.getClassName(), other);
                }
            }
        }
        
        return -1;
    }
    

    /**
     * Look for {@link org.jboss.seam.servlet.SeamFilter.FilterChainImpl#doFilter(ServletRequest, ServletResponse)} in 
     * the stacktrace, and return its index, or -1 if it doesn't exist.
     * @param stackTrace
     * @return
     */
    private int getSeamFilterEndLocation(List<StackTraceElement> stackTrace) {
        for (int i = 0; i < stackTrace.size(); i++) {
            StackTraceElement element = stackTrace.get(i);
            if (element.getMethodName().equals("doFilter")) {
                try {
                    if (isSeamFilterChainClass(Class.forName(element.getClassName()))) {
                        return i;
                    }
                } catch (ClassNotFoundException cnf) {
                    //this often happens for app-server classes
                    log.trace("class not found: " + element.getClassName());
                } catch (Exception other) {
                    log.info("exception while trying to find class " + element.getClassName(), other);
                }
            }
        }
        return -1;
    }

    /**
     * Can't directly access SeamFilter.FilterChainImpl to do an isAssignableFrom check, so use the fact that we
     * know its enclosing class is SeamFilter, and it implements FilterChain
     * 
     * @param filterClass
     * @return
     */
    private boolean isSeamFilterChainClass(Class<?> filterClass)
    {
        return FilterChain.class.isAssignableFrom(filterClass) && 
                filterClass.getEnclosingClass() != null &&
                SeamFilter.class.isAssignableFrom(filterClass.getEnclosingClass());
    }


    public static StacktraceReducer instance()
    {
        return Components.getStatelessComponent(StacktraceReducer.class);
    }

}
