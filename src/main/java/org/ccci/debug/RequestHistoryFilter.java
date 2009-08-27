package org.ccci.debug;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ccci.util.seam.Components;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.web.AbstractFilter;

@Name("requestHistoryFilter")
@Filter(around = "exceptionContextFilter")
@Scope(APPLICATION)
@org.jboss.seam.annotations.intercept.BypassInterceptors
public class RequestHistoryFilter extends AbstractFilter
{

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException
    {
        filterChain.doFilter(request, response);
        if (request instanceof HttpServletRequest)
        {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpSession session = httpRequest.getSession(false);
            if (session != null)
            {
                RequestHistory requestHistory = Components.getSessionScopedComponentOutsideSessionContext(RequestHistory.class, session);
                if (requestHistory != null)
                {
                    requestHistory.newRequest(httpRequest);
                }
            }
        }
    }
}
