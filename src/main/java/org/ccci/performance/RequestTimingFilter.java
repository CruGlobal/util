package org.ccci.performance;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.log.Log;
import org.jboss.seam.web.AbstractFilter;

@Name("org.ccci.requestTimingFilter")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Install(false)
@Filter(within = "org.jboss.seam.web.ajax4jsfFilter")
public class RequestTimingFilter extends AbstractFilter
{

	@Logger	Log log;
	
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) 
		throws IOException, ServletException {
		long beginRequest = System.currentTimeMillis();
		try
		{
		    filterChain.doFilter(request, response);
		}
		finally
		{
		    long endRequest = System.currentTimeMillis();
		    log.debug("Request finished in #0 ms", endRequest - beginRequest);
		}
	}

}
