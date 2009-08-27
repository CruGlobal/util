package org.ccci.debug;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Same as {@link org.jboss.seam.web.ExceptionFilter}, but doesn't log exceptions at error level, which can cause
 * unnecessary emails.
 * 
 * @author Matt Drees
 * 
 */
@Scope(APPLICATION)
@Name("org.jboss.seam.web.exceptionFilter")
@Install(precedence = Install.FRAMEWORK, classDependencies = "javax.faces.context.FacesContext")
@BypassInterceptors
@Filter(within = "org.jboss.seam.web.ajax4jsfFilter")
public class ExceptionFilter extends org.jboss.seam.web.ExceptionFilter
{
    private static final LogProvider log = Logging.getLogProvider(ExceptionFilter.class);

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException
    {
        try
        {
            chain.doFilter(request, response);
        }
        catch (Exception e)
        {
            log.debug("handling uncaught exception: " + e);
            log.debug("exception root cause: " + org.jboss.seam.util.Exceptions.getCause(e));
            endWebRequestAfterException((HttpServletRequest) request, (HttpServletResponse) response, e);
        }
    }

}
