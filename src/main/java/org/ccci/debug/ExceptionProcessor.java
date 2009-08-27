package org.ccci.debug;

import static org.jboss.seam.ScopeType.STATELESS;

import javax.servlet.http.HttpSession;

import org.ccci.util.seam.Components;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.google.common.base.Preconditions;

@Name("exceptionProcessor")
@Scope(STATELESS)
@BypassInterceptors
public class ExceptionProcessor
{

    private Log log = Logging.getLog(ExceptionProcessor.class);

    /**
     * Check for exceptions that were thrown during this request. Notify developers, if necessary. Also add to
     * session-scoped {@link ExceptionHistory}
     * 
     * Should not throw exceptions (this method is called in a finally block, so if it throws an exception, it may
     * shadow another thrown exception, which is evil). So Log and swallow. (Also, hope that we don't create an
     * Error, which we don't catch. An Error will shadow another thrown exception or throwable. But, it's not good
     * to swallow Errors)
     * 
     * @param thrown
     *            if the request is ended by throwing a throwable to the Servlet Container, this parameter holds
     *            that throwable. Otherwise, it is <tt>null</tt>.
     */
    public void processExceptionsAtEndOfRequest(Throwable thrown)
    {
        try
        {
            Preconditions.checkState(ExceptionContext.isExceptionContextAvailable());
            ExceptionContext exceptionContext = ExceptionContext.getCurrentInstance();
    
            //TODO: check to see if errors were logged, and send
            //some kind of report even if no exceptions were thrown
            if (!exceptionContext.didExceptionsOccur() && !exceptionContext.didErrorLogMessagesOccur())
            {
                if (thrown != null)
                {
                    log.error("no exception recorded or handled during request, but one is propagating out of application",
                        thrown);
                }
                return;
            }
    
            if (exceptionContext.didExceptionsOccur())
            {
                handleEndedConversation(exceptionContext);
        
                ExceptionEvent event = exceptionContext.getPrimaryException();
                ExceptionFloodGuard floodGuard = ExceptionFloodGuard.instance();
                floodGuard.recordExceptionEvent(event);
                if (floodGuard.isFloodingException(event.getExceptionLocation()))
                {
                    ExceptionLogger.instance().logFloodingExceptions();
                }
                else
                {
                    StacktraceReducer.instance().replaceStackTraceWithReducedStackTrace(event.getThrowable());
                    ExceptionLogger.instance().logExceptions();
                    ExceptionReporter.instance().reportExceptions();
                }
            }
            else if (exceptionContext.didErrorLogMessagesOccur())
            {
                ExceptionReporter.instance().reportLogMessages();
            }
        }
        catch (Exception e)
        {
            log.error("Exception occurred during exception processing; swallowing", e);
        }

    }


    private void handleEndedConversation(ExceptionContext exceptionContext)
    {
        HttpSession session = exceptionContext.getSession();
        if (session == null)
        {
            return;
        }
        ExceptionHistory exceptionHistory =
                Components.getSessionScopedComponentOutsideSessionContext(ExceptionHistory.class,
                    session);
        ConversationEndedObserver conversationEndedObserver =
                Components.getEventScopedComponentOutsideEventContext(ConversationEndedObserver.class,
                    exceptionContext.getRequest());

        if (conversationEndedObserver != null && conversationEndedObserver.wasConversationEndedByException())
        {
            exceptionHistory.addConversationIdKilledByException(conversationEndedObserver.getEndedConversationId());
        }
    }

    
    public static ExceptionProcessor instance()
    {
        return Components.getStatelessComponent(ExceptionProcessor.class);
    }
    
}
