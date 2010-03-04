package org.ccci.performance;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.ccci.util.HttpRequests;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.log.Log;
import org.jboss.seam.web.AbstractFilter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

@Name("org.ccci.requestTimingFilter")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Install(false)
@Filter(within = "org.jboss.seam.web.ajax4jsfFilter")
public class RequestTimingFilter extends AbstractFilter
{

	@Logger	Log log;
	

    /**
     * Contains a list of regular expressions that specify which urls should ignored by this timing filter.
     * Some urls are hit very frequently (for example, a page that is checked by a monitoring or loadbalancing system).
     */
    private List<String> urlPatternsToIgnore = Lists.newArrayList();
    
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) 
		throws IOException, ServletException {

        boolean ignore = false;
        if (request instanceof HttpServletRequest)
        {
            String fullPath = HttpRequests.getFullPath((HttpServletRequest) request);
            for (String urlPatternToIgnore : urlPatternsToIgnore)
            {
                //possible optimization: pre-compile these into a single regex
                if (fullPath.matches(urlPatternToIgnore))
                {
                    ignore = true;
                }
            }
        }
        
        long beginRequest;
        if (!ignore)
        {
            beginRequest = System.currentTimeMillis();
        }
        else
        {
            beginRequest = 0;  // only to make compiler happy
        }
        
		try
		{
		    filterChain.doFilter(request, response);
		}
		finally
		{
		    if (!ignore)
		    {
		        long endRequest = System.currentTimeMillis();
		        log.debug("Request finished in #0 ms", endRequest - beginRequest);
		    }
		}
	}
	

    public List<String> getUrlPatternsToIgnore()
    {
        return urlPatternsToIgnore;
    }

    public void setUrlPatternsToIgnore(List<String> urlPatternsToIgnore)
    {
        Preconditions.checkNotNull(urlPatternsToIgnore, "urlPatternsToIgnore is null");
        this.urlPatternsToIgnore = urlPatternsToIgnore;
    }

}
