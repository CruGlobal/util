package org.ccci.debug;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.web.AbstractFilter;

@Name("recordedExceptionsFilter")
@Scope(APPLICATION)
@org.jboss.seam.annotations.intercept.BypassInterceptors
@Filter(around = "org.jboss.seam.web.exceptionFilter", within = "org.jboss.seam.web.ajax4jsfFilter")
public class RecordedExceptionsFilter extends AbstractFilter
{

    @Override
    public void doFilter(ServletRequest reqest, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException
    {
        filterChain.doFilter(reqest, response);
        if (Contexts.isEventContextActive())
        {
            //in general, it shouldn't be.  If it is, then it has been left dangling, and
            //RecordedExceptions#checkForSwallowedExceptions() will never be called.  So call it.
            RecordedExceptions.instance().checkForSwallowedExceptions();
        }
    }

}
