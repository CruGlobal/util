package org.ccci.debug;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.ViewExpiredException;
import javax.faces.convert.ConverterException;
import javax.faces.event.PhaseId;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.sun.faces.context.FacesFileNotFoundException;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.ccci.util.seam.Components;
import org.jboss.seam.annotations.ApplicationException;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.FacesLifecycle;
import org.jboss.seam.core.Manager;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.util.EJB;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * Throughout the course of a Request, there is a {@link ThreadLocal} holding an ExceptionContext object.
 * This is used to record exceptions as they occur, and to analyze the exceptions at the end
 * of the request.
 * @author Matt Drees
 *
 */
public class ExceptionContext
{

    private final static ThreadLocal<ExceptionContext> exceptionContext = new ThreadLocal<ExceptionContext>(); 

    private final HttpServletRequest request;
    
    /**
     * Contains ExceptionEvents for all exceptions that were detected this request, either from {@link #recordHandledException(Throwable, String)}
     * or {@link #recordThrownException(Throwable)}.  May actually contain some application exceptions (application
     * exceptions don't cause rollbacks, and have some "business" meaning.  Usually they are checked exceptions, or
     * exceptions marked with {@link ApplicationException}
     */
    private final Map<Throwable, ExceptionEvent> exceptionEvents = Maps.newLinkedHashMap();
    private final Map<Throwable, HandledExceptionEvent> handledExceptionEvents = Maps.newLinkedHashMap();
    
    private final List<LoggingEvent> loggingEvents = Lists.newArrayList();
    private Layout loggingLayout;


    protected ExceptionContext(HttpServletRequest request)
    {
        this.request = request;
    }

    public ExceptionEvent recordThrownException(Throwable throwable)
    {
        Preconditions.checkNotNull(throwable);
        Throwable rootThrowable = ExceptionUtil.getRootThrowable(throwable);
        if (!exceptionEvents.containsKey(rootThrowable))
        {
            ExceptionEvent exceptionEvent = buildExceptionEvent(throwable);
            addContext(exceptionEvent);
            exceptionEvents.put(rootThrowable, exceptionEvent);
            return exceptionEvent;
        }
        return exceptionEvents.get(rootThrowable);
    }

    private void addContext(ExceptionEvent exceptionEvent)
    {
        //TODO:
        //add staff account number?
        //exceptionEvent.addContextInformation("designation", "1234567");
    }

    private ExceptionEvent buildExceptionEvent(Throwable throwable)
    {
        return new ExceptionEvent(throwable, getCurrentDateTime(), getSessionId(), 
            getUsername(), getPhaseId(), getConversationId(), getUserAgent(), getRemoteIPAddress());
    }
    
    private String getUserAgent()
    {
        return request.getHeader("User-Agent");
    }

    public void recordHandledException(Throwable throwable, String handledBy)
    {
        ExceptionEvent exceptionEvent = recordThrownException(throwable);
        handledExceptionEvents.put(ExceptionUtil.getRootThrowable(throwable), new HandledExceptionEvent(exceptionEvent, handledBy));
    }
    
    public String getUsername()
    {
        HttpSession session = getSession();
        if (session == null)
        {
            return null;
        }
        Credentials credentials = Components.getSessionScopedComponentOutsideSessionContext(Credentials.class, session);
        return credentials == null ? null : credentials.getUsername();
    }

    private String getSessionId()
    {
        return getSession() == null ? null : getSession().getId();
    }

    public Map<Throwable, ExceptionEvent> getNonhandledSystemExceptions()
    {
        Map<Throwable, ExceptionEvent> unhandledExceptions = Maps.newLinkedHashMap();
        for (Map.Entry<Throwable, ExceptionEvent>  entry : exceptionEvents.entrySet())
        {
            Throwable outermostThrowable = entry.getValue().getThrowable();
            if (chainContainsNonApplicationException(outermostThrowable))
            {
                unhandledExceptions.put(entry.getKey(), entry.getValue());
            }
        }
        
        unhandledExceptions.keySet().removeAll(handledExceptionEvents.keySet());
        return unhandledExceptions;
    }

    private boolean chainContainsNonApplicationException(Throwable outermostThrowable)
    {
        for (Throwable throwable : ExceptionUtil.eachThrowableInChain(outermostThrowable))
        {
            if (!(throwable instanceof Exception) || isSystemException((Exception) throwable))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get all the unexpected exceptions that occurred during this request.
     * "Unexpected exceptions" are defined to be the union of exceptions that may have
     * caused a rollback (i.e. "system exceptions") and exceptions that were handled by the exception handler,
     * minus a list of specific exceptions that are ignored (see {@link #isIgnorable(ExceptionEvent)}).
     * 
     * 
     * May have been reported by {@link #recordThrownException(Throwable)}
     * or by {@link #recordHandledException(Throwable, String)}.
     * 
     * @return
     */
    public Map<Throwable, ExceptionEvent> getUnexpectedExceptions()
    {
        Map<Throwable, ExceptionEvent> unexpectedExceptions = Maps.newLinkedHashMap();
        for (Map.Entry<Throwable, ExceptionEvent> entry : exceptionEvents.entrySet())
        {
            Throwable rootThrowable = entry.getKey();
            if ((chainContainsNonApplicationException(entry.getValue().getThrowable()) || 
                    handledExceptionEvents.containsKey(rootThrowable))
                && !isIgnorable(rootThrowable)
                )
            {
                unexpectedExceptions.put(rootThrowable, entry.getValue());
            }
        }
        return unexpectedExceptions;
    }

    // note: copy/pasted from org.jboss.seam.transaction.RollbackInterceptor.java.  No public api.
    private boolean isSystemException(Exception e)
    {
        return (e instanceof RuntimeException) && !e.getClass().isAnnotationPresent(EJB.APPLICATION_EXCEPTION)
                && !e.getClass().isAnnotationPresent(ApplicationException.class) &&
                !(e instanceof ValidatorException) && !(e instanceof ConverterException);
    }
    

    /**
     * For now, a static hard coded list.  Maybe make extensible in the future.
     */
    private final static Set<Class<? extends Throwable>> IGNORED_THROWABLE_TYPES = 
        ImmutableSet.<Class<? extends Throwable>>of(
            ViewExpiredException.class, 
            AuthorizationException.class,
            BackButtonException.class,
            FacesFileNotFoundException.class);

    /**
     * Some exceptions don't warrant an exception notification, and result from rare, recoverable circumstances
     * or client errors that cannot be prevented
     * @param rootThrowable
     * @return
     */
    private boolean isIgnorable(Throwable rootThrowable)
    {
        
        for (Class<? extends Throwable> ignoredThrowableType : IGNORED_THROWABLE_TYPES)
        {
            if (ignoredThrowableType.isInstance(rootThrowable))
            {
                return true;
            }
        }
        return false;
    }

    public Multimap<ExceptionLocation, ExceptionEvent> getUnexpectedExceptionsByLocation()
    {
        Multimap<ExceptionLocation, ExceptionEvent> unexpectedExceptionsByLocation = LinkedHashMultimap.create();
        for (ExceptionEvent event : getUnexpectedExceptions().values())
        {
            unexpectedExceptionsByLocation.put(event.getExceptionLocation(), event);
        }
        return unexpectedExceptionsByLocation;
    }
    
    public Map<Throwable, HandledExceptionEvent> getHandledExceptions()
    {
        return Collections.unmodifiableMap(handledExceptionEvents);
    }
    
    /**
     * Overrideable for testability
     * @return
     */
    protected String getConversationId()
    {
        if (Contexts.isEventContextActive())
        {
            return Manager.instance().getCurrentConversationId();
        }
        return null;
    }
    /**
     * overrideable for testability
     * @return
     */
    protected PhaseId getPhaseId()
    {
        return FacesLifecycle.getPhaseId();
    }

    /**
     * overrideable for testability
     * @return
     */
    protected DateTime getCurrentDateTime()
    {
        return new DateTime();
    }

    /*
     *  Static stuff
     */
    
    public static void start(HttpServletRequest request)
    {
        Preconditions.checkState(!isExceptionContextAvailable());
        exceptionContext.set(new ExceptionContext(request));
    }

    public static void end()
    {
        Preconditions.checkState(isExceptionContextAvailable());
        exceptionContext.remove();
    }

    public static boolean isExceptionContextAvailable()
    {
        return exceptionContext.get() != null;
    }

    public static ExceptionContext getCurrentInstance()
    {
        Preconditions.checkState(isExceptionContextAvailable(), "ExceptionContext is not available");
        return exceptionContext.get();
    }

    public void recordLoggingEvent(LoggingEvent event)
    {
        loggingEvents.add(event);        
    }

    public void setLoggingLayout(Layout loggingLayout)
    {
        Preconditions.checkArgument(this.loggingLayout == null || this.loggingLayout.equals(loggingLayout),
            "exception reporter does not support multiple layouts");
        this.loggingLayout = loggingLayout;
    }

    public Layout getLoggingLayout()
    {
        return loggingLayout;
    }
    
    public HttpServletRequest getRequest()
    {
        return request;
    }
    
    public String getRemoteIPAddress()
    {
        return request.getRemoteAddr();
    }
    
    public HttpSession getSession()
    {
        return request.getSession(false);
    }

    public List<LoggingEvent> getLoggingEvents()
    {
        return loggingEvents;
    }

    public boolean didExceptionsOccur()
    {
        return !getUnexpectedExceptions().isEmpty();
    }

    /**
     * The exception that occurred first; most likely the root of any
     * problems that occur later in the request.  Will not return null.
     * 
     * @return
     */
    public ExceptionEvent getPrimaryException()
    {
        Preconditions.checkState(!getUnexpectedExceptions().values().isEmpty(), "system exception value set is empty: %s", getUnexpectedExceptions());
        return Collections.min(getUnexpectedExceptions().values());
    }

    /**
     * The exception that was handled first (usually, only one exception will be handled per request unless something
     * really goofy happens).
     * If no exception was handled, returns null.
     * @return
     */
    public HandledExceptionEvent getPrimaryHandledException()
    {
        if (getHandledExceptions().isEmpty()) return null;
        return Collections.min(getHandledExceptions().values());
    }

    public boolean didErrorLogMessagesOccur()
    {
        for (LoggingEvent event : loggingEvents)
        {
            if (event.getLevel().toInt() >= Level.ERROR_INT) return true;
        }
        return false;
    }
    
}
