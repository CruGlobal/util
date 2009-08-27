package org.ccci.debug;

import static org.jboss.seam.ScopeType.STATELESS;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ccci.util.seam.Components;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

@Name("exceptionLogger")
@Scope(STATELESS)
@BypassInterceptors
public class ExceptionLogger
{

    @Logger Log log;

    public void logExceptions()
    {
        ExceptionContext exceptionContext = ExceptionContext.getCurrentInstance();
        
        ExceptionEvent primaryException = exceptionContext.getPrimaryException();
        log.error("Exception occurred during request: ", primaryException.getThrowable());

        AdditionalExceptionDetailsPrinter printer = AdditionalExceptionDetailsPrinter.instance();
        
        List<String> additionalDetails = printer.getAdditionalDetails(primaryException.getThrowable());
        if (! additionalDetails.isEmpty())
        {
            log.info("Additional exception details:");
            for (String additionalDetalLine : additionalDetails)
            {
                log.info(additionalDetalLine);
            }
        }
        
        Map<Throwable, HandledExceptionEvent> handledExceptions = exceptionContext.getHandledExceptions();
        boolean primaryWasHandled = false;
        if (!handledExceptions.isEmpty() && handledExceptions.keySet().contains(primaryException.getRootThrowable()))
        {
            HandledExceptionEvent primaryHandledException = handledExceptions.get(primaryException.getRootThrowable());
            log.info("It was handled by the #0", primaryHandledException.getHandledBy());
            primaryWasHandled = true;
        
            checkForOtherHandledExceptions(primaryException, handledExceptions);
        }
        else
        {
            log.info("It was not handled.");
            if (!handledExceptions.isEmpty())
            {
                log.info("the primary handled exception:");
                for (String summaryLine : ExceptionUtil.summarizeException(exceptionContext.getPrimaryHandledException().getHandledThrowable()))
                {
                    //TODO: if summaryLine contains an EL expression (as some facelets errors do), this causes and extra stacktrace when
                    //seam tries to interpolate the expression.  Probably, instead of using seam's Log class, should be using LogProvider
                    //directly without interpolation.
                    log.info(summaryLine);
                }
                checkForOtherHandledExceptions(primaryException, handledExceptions);
            }
        }
        
        Map<Throwable, ExceptionEvent> unhandledSystemExceptions = exceptionContext.getNonhandledSystemExceptions();
        if (!unhandledSystemExceptions.isEmpty())
        {
            String message = "there were #0 nonhandled system exceptions";
            if (!primaryWasHandled)
            {
                message += " (including the primary exception)";
            }
            message += ", categorized below";
            log.info(message, unhandledSystemExceptions.size());
        }
        Multimap<ExceptionLocation, ExceptionEvent> systemExceptionsByLocation = exceptionContext.getUnexpectedExceptionsByLocation();
        for (ExceptionLocation exceptionLocation : systemExceptionsByLocation.keySet())
        {
            log.info("#0 exceptions of type #1 occurred at #2",
                systemExceptionsByLocation.get(exceptionLocation).size(), 
                exceptionLocation.getThrowableClass(),
                exceptionLocation.getLocation());
        }
    
    }

    private void checkForOtherHandledExceptions(ExceptionEvent primaryException,
                                                Map<Throwable, HandledExceptionEvent> handledExceptions)
    {
        if (handledExceptions.size() > 1)
        {
            log.info("More than one exception was handled");
            Set<HandledExceptionEvent> others = Sets.newHashSet(handledExceptions.values());
            others.remove(primaryException);
            for (HandledExceptionEvent other : others)
            {
                log.error("other handled exception:" , other.getHandledThrowable());
            }
        }
    }

    public void logFloodingExceptions()
    {
        ExceptionContext exceptionContext = ExceptionContext.getCurrentInstance();
        ExceptionEvent primaryException = exceptionContext.getPrimaryException();
        log.warn("A flooding exception occured: #0", primaryException.toString());
    }

    
    public static ExceptionLogger instance()
    {
        return Components.getStatelessComponent(ExceptionLogger.class);
    }
}
