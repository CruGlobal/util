package org.ccci.debug;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;

import org.jboss.seam.util.EJB;
import org.jboss.seam.util.Reflections;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

//TODO: move to util.Exceptions
public class ExceptionUtil
{

    /**
     * code copied from {@link EJB#getCause(Exception)}, but supports {@link Throwable}s
     * @param throwable
     * @return
     */
    public static Throwable getCause(Throwable throwable)
    {
        Throwable cause = null;
          try
          {
             if ( EJB.EJB_EXCEPTION.isInstance(throwable) )
             {
                cause = (Throwable) Reflections.getGetterMethod(EJB.EJB_EXCEPTION, "causedByException").invoke(throwable);
             }
             else if (throwable instanceof ServletException)
             {
                cause = getServletExceptionCause((ServletException) throwable);
             }
             else
             {
                cause = throwable.getCause();
             }
          }
          catch (Exception x)
          {
             return null;
          }
          return cause==throwable ? null : cause;
    }

    /**
     * It's hard to tell whether a {@link ServletException}'s actual root cause is {@link Throwable#getCause(),
     * or {@link ServletException#getRootCause()}, so return whichever is non-null, giving priority
     * to {@code ServletException#getRootCause()}
     * @param servletException
     * @return
     */
    public static Throwable getServletExceptionCause(ServletException servletException)
    {
        if (servletException.getCause() != null) return servletException.getCause();
        return servletException.getRootCause();
    }
    
    public static Throwable getRootThrowable(Throwable throwable)
    {
        Preconditions.checkNotNull(throwable, "throwable is null");
        Throwable root = throwable;
        while (getCause(root) != null)
        {
            root = getCause(root);
        }
        return root;
    }
    
    public static Iterable<Throwable> eachThrowableInChain(final Throwable throwable)
    {
        
        return new Iterable<Throwable>()
        {
            
            public Iterator<Throwable> iterator()
            {
                return new Iterator<Throwable>()
                {
                    private Throwable next = throwable;

                    public boolean hasNext()
                    {
                        return next != null;
                    }

                    public Throwable next()
                    {
                        if (!hasNext())
                        {
                            throw new NoSuchElementException();
                        }
                        Throwable current = next;
                        next = getCause(next); 
                        return current;
                    }

                    public void remove()
                    {
                        throw new UnsupportedOperationException("cannot remove");
                    }
                
                };
            }
        };
    }

    /**
     * <p>Print a very short stacktrace, where each throwable in the chain
     * is printed, but only the first stackframe is printed.
     * </p>
     * 
     * E.G.: 
     * <pre>
     * java.lang.IllegalArgumentException: null argument
     *   at MyClass.doStuff(MyClass.java:123), caused by:
     * java.lang.NullPointerException: null
     *   at MyOtherClass.doOtherStuff(MyOtherClass.java:321)
     * </pre>
     * @param throwable
     * @return
     */
    public static List<String> summarizeException(Throwable throwable)
    {
        ArrayList<String> summaryLines = Lists.newArrayList();
        for (Throwable link : eachThrowableInChain(throwable))
        {
            summaryLines.add(link.toString());
            StackTraceElement[] stackTrace = link.getStackTrace();
            String stacktracePart;
            if (stackTrace.length == 0)
            {
                stacktracePart = "(stacktrace unavailable)";
            } else {
                stacktracePart = "at " + stackTrace[0].toString();
            }
            String message = "  ";
            if (getCause(link) != null)
            {
                message += stacktracePart + ", caused by:";
            } else {
                message += stacktracePart;
            }
            summaryLines.add(message);
        }
        return summaryLines;
    }

    public static String printStacktraceToString(Throwable throwable)
    {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        throwable.printStackTrace();
        //ServletException#getCause sometimes returns null, and sometimes returns #getRootCause().  So, check if we need
        //to manually print out the root cause
        if (throwable instanceof ServletException)
        {
            ServletException servletException = (ServletException) throwable;
            if (servletException.getCause() == null || servletException.getCause() != servletException.getCause())
            {
                printWriter.println("Root cause follows.");
                printWriter.println(printStacktraceToString(getServletExceptionCause(servletException)));
            }
        }
        return writer.toString();
    }

}
