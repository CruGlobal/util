package org.ccci.debug;

import static org.jboss.seam.util.EJB.APPLICATION_EXCEPTION;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.faces.event.PhaseId;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.ApplicationException;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.FacesLifecycle;
import org.jboss.seam.log.Log;
import org.jboss.seam.util.JSF;
import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Keeps record of system exceptions (runtime exceptions not marked {@link ApplicationException} or {@link javax.ejb.ApplicationException})
 * thrown during a request, and exceptions handled by Seam or Facelets.
 * 
 * TODO:  resolve the duplication between this and {@link ExceptionContext}
 * 
 * @author Matt Drees
 *
 */
@Name("thrownExceptions")
@AutoCreate
public class RecordedExceptions
{

    private @Logger Log log;

    private Map<Exception, Occurance> occurances = Maps.newLinkedHashMap();

    private Set<Throwable> handledExceptions = Sets.newLinkedHashSet();
    
    private int maxSwallowedExceptionsToPrint = 2;
    
    /**
     * might return null if Event context is nonexistent
     * @return
     */
    public static RecordedExceptions instance()
    {
        return (RecordedExceptions) Component.getInstance(RecordedExceptions.class, ScopeType.EVENT);
    }

    public void recordThrownException(Exception e)
    {
        if (isSystemException(e, e.getClass()))
        {
            if (!occurances.containsKey(e))
            {
                occurances.put(e, new Occurance());
            }
        }
    }
    
    public boolean wereSystemExceptionsThrown()
    {
        return !occurances.isEmpty();
    }

    public Map<Exception, Occurance> getSystemExceptions()
    {
        return Collections.unmodifiableMap(occurances);
    }
    
    public Map<Exception, Occurance> getUnhandledExceptions()
    {
        Map<Exception, Occurance> unhandledExceptions = Maps.newLinkedHashMap(occurances);
        unhandledExceptions.keySet().removeAll(handledExceptions);
        return unhandledExceptions;
    }
    
    // note: copy/pasted from org.jboss.seam.transaction.Rollbackinterceptor.java.  No public api.
    private boolean isSystemException(Exception e, Class<? extends Exception> clazz)
    {
        return (e instanceof RuntimeException) && !clazz.isAnnotationPresent(APPLICATION_EXCEPTION)
                && !clazz.isAnnotationPresent(ApplicationException.class) &&
                !JSF.VALIDATOR_EXCEPTION.isInstance(e) && !JSF.CONVERTER_EXCEPTION.isInstance(e);
    }

    public void recordHandledException(Exception e)
    {
        handledExceptions.add(ExceptionUtil.getRootThrowable(e));
    }

    public class Occurance
    {
        public DateTime occuredAt = new DateTime();
        
        public PhaseId phaseId = FacesLifecycle.getPhaseId();

        public DateTime getOccuredAt()
        {
            return occuredAt;
        }

        public PhaseId getPhaseId()
        {
            return phaseId;
        } 
    }
    
    @Destroy
    public void checkForSwallowedExceptions()
    {
        if (!getUnhandledExceptions().isEmpty())
        {
            int counter = 0;
            Set<Entry<Exception, Occurance>> entrySet = getUnhandledExceptions().entrySet();
            for (Entry<Exception, Occurance> entry : entrySet)
            {
                if (counter < maxSwallowedExceptionsToPrint)
                {
                    Occurance occurance = entry.getValue();
                    if (occurance.getPhaseId() == null)
                    {
                        log.warn("potentially swallowed exception, first recorded at #0 outside SeamPhaseListener's scope", entry.getKey(), occurance.getOccuredAt());
                    }
                    else
                    {
                        log.warn("potentially swallowed exception, first recorded at #0 during phase #1", entry.getKey(), occurance.getOccuredAt(), occurance.getPhaseId());
                    }
                }
                counter++;
            }
            if (entrySet.size() > maxSwallowedExceptionsToPrint)
            {
                log.warn("there were #0 potentially swallowed exceptions; only the first #1 were printed", entrySet.size(), maxSwallowedExceptionsToPrint);
            }
        }
    }

    public int getMaxSwallowedExceptionsToPrint()
    {
        return maxSwallowedExceptionsToPrint;
    }

    public void setMaxSwallowedExceptionsToPrint(int maxSwallowedExceptionsToPrint)
    {
        this.maxSwallowedExceptionsToPrint = maxSwallowedExceptionsToPrint;
    }

    public void setLog(Log log)
    {
        this.log = log;
    }
    
}
