package org.ccci.debug;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.ccci.util.seam.Components;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * If tons of {@link ExceptionLocation}s start happening, we call it a Flood.  The particular ExceptionOccurance
 * that is causing the flood is called the FloodingException.
 * 
 * 
 * @author Matt Drees
 *
 */

@Name("exceptionFloodGuard")
@BypassInterceptors
@Scope(APPLICATION)
public class ExceptionFloodGuard
{

    private ReadWriteLock exceptionListLock = new ReentrantReadWriteLock();
    
    private Multimap<ExceptionLocation, ExceptionEvent> exceptionsByLocation = LinkedHashMultimap.create();
    
    public void recordExceptionEvent(ExceptionEvent event)
    {
        exceptionListLock.writeLock().lock();
        try
        {
            //TODO: finish
        }
        finally
        {
            exceptionListLock.writeLock().unlock();
        }
    }
    
    public boolean isFloodingException(ExceptionLocation exceptionLocation)
    {
        exceptionListLock.readLock().lock();
        try
        {
            @SuppressWarnings("unused")
            Collection<ExceptionEvent> events = exceptionsByLocation.get(exceptionLocation);
            return false; //TODO: finish
            
        }
        finally
        {
            exceptionListLock.readLock().unlock();
        }
        
    }
    
    public FloodSummary createFloodSummary(ExceptionLocation exceptionLocation)
    {
        exceptionListLock.readLock().lock();
        try
        {
            return null; //TODO: finish
        }
        finally
        {
            exceptionListLock.readLock().unlock();
        }
    }
    
    public static ExceptionFloodGuard instance()
    {
        return Components.getStatelessComponent(ExceptionFloodGuard.class);
    }
}
