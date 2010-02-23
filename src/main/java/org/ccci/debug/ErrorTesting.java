package org.ccci.debug;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.ccci.debug.LowMemoryNotifier.LowMemoryReport;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

/**
 * To test error handling in a given application, can wire up a page to this component.  For example,
 * in the app's pages.xml:
 * 
    <page view-id="/bang.xhtml" action="#{errorTesting.throwRuntimeException}"/>
    
    <page view-id="/logError.xhtml" action="#{errorTesting.logError}"/>
    
    <page view-id="/destroyMemory.xhtml" action="#{errorTesting.createOutOfMemoryError}"/>

 * @author Matt Drees
 *
 */
@Name("errorTesting")
public class ErrorTesting
{

    @Logger Log log;
    
    public void throwSQLException(String message) throws SQLException {
        log.debug("creating test sql exception");
        throw new SQLException(message);
    }
    
	public void throwRuntimeException() {
	    log.debug("creating test exception");
		throw new RuntimeException("Test exception generated at " + new Date());
	}
	
	/**
	 * For view exception testing
	 */
	public String getPropertyThatExplodes() {
		throwRuntimeException();
		return null;
	}
	
	/**
	 * For error log reporting testing
	 */
	public void logError()
	{
	    log.error("test error logged at " + new Date());
	}
	
	/**
	 * To test {@link LowMemoryReport}.  
	 * From http://www.javaspecialists.co.za/archive/Issue092.html
	 * 
	 * Chews up memory until it's almost gone, and then waits for 20 seconds.
	 * 
	 * @throws InterruptedException 
	 */
	public void createOutOfMemoryError() throws InterruptedException
	{
	    Collection<Double> numbers = new LinkedList<Double>();
	    while (true) {
	        for (int i = 0; i < 1000000; i++)
	        {
	            numbers.add(Math.random());
	        }
	        Thread.sleep(100);
	        long freeMemory = Runtime.getRuntime().freeMemory();
	        long totalMemory = Runtime.getRuntime().totalMemory();
	        long maxMemory = Runtime.getRuntime().maxMemory();
	        if (((double) (maxMemory - totalMemory + freeMemory))/ maxMemory < .10)
	        {
	            log.debug("memory almost exhausted; free: #0, total: #1, max: #2", freeMemory, totalMemory, maxMemory);
	            log.debug("sleeping for 20 seconds");
                Thread.sleep(20 * 1000);
                return;
	        }
	    }

	}
	
	public void slowRequest()
	{
	    try
        {
            Thread.sleep(12 * 1000);
        }
        catch (InterruptedException e)
        {
            return;
        }
	}

}
