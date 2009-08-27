package org.ccci.debug;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.web.AbstractFilter;

@Name("exceptionContextFilter")
@Scope(APPLICATION)
@org.jboss.seam.annotations.intercept.BypassInterceptors
@Filter(around = "org.jboss.seam.web.exceptionFilter", within = "org.jboss.seam.web.ajax4jsfFilter")
public class ExceptionContextFilter extends AbstractFilter
{

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException
    {
        if ( !(request instanceof HttpServletRequest))
        {
            filterChain.doFilter(request, response);
            return;
        }
        
        ExceptionContext.start((HttpServletRequest) request);
        Throwable thrown = null;
        try 
        {
            filterChain.doFilter(request, response);
        }
        catch (IOException e)
        {
            thrown = e;
            throw e;
        }
        catch (ServletException e)
        {
            thrown = e;
            throw e;
        }
        catch (RuntimeException e)
        {
            thrown = e;
            throw e;
        }
        catch (Error e)
        {
            thrown = e;
            throw e;
        }
        finally
        {
            try
            {
                ExceptionProcessor.instance().processExceptionsAtEndOfRequest(thrown);
            }
            finally
            {
                ExceptionContext.end();
            }
        }
    }


}
