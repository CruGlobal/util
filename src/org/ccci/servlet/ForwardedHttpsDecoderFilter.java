package org.ccci.servlet;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.ccci.util.strings.Strings;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

/**
 * See http://www.jboss.com/index.html?module=bb&op=viewtopic&p=4126531#4126531
 *
 * BigIp translates https requests into http requests, so we need to simulate http for
 * redirect rendering, etc.
 * 
 * Note:  this is only necessary if we don't have an ajp port configured in tomcat to act like an ssl port
 * (configured in server.xml)
 * 
 * @author Matt Drees
 *
 */
public class ForwardedHttpsDecoderFilter implements Filter
{

    public static final String HEADER_HTTP_X_FORWARDED_PROTO = "HTTP_X_FORWARDED_PROTO";

    private final static Log log = Logging.getLog(ForwardedHttpsDecoderFilter.class);
    
    static class SslRequest extends HttpServletRequestWrapper
    {

        public SslRequest(HttpServletRequest request)
        {
            super(request);
        }

        public HttpServletRequest getRequest()
        {
            return (HttpServletRequest) super.getRequest();
        }

        public StringBuffer getRequestURL()
        {
            StringBuffer requestURL = super.getRequestURL();
            if (requestURL.indexOf("http://") == 0)
            {
                requestURL.replace(0, 7, "https://");
            }
            return requestURL;
        }
        
        public String getScheme() {
            return "https";
        }
        
        @Override
        public boolean isSecure()
        {
            return true;
        }
    }
    
    static class SslResponse extends HttpServletResponseWrapper
    {

        static final Pattern ABSOLUTE_URL_PATTERN = Pattern.compile("\\w+:.*");
        private final SslRequest request;

        public SslResponse(HttpServletResponse response, SslRequest request)
        {
            super(response);
            this.request = request;
        }
        
        /**
         * Need to tweak this, because the servlet container will fill in the wrong scheme when generating
         * an absolute url.  So, need to always construct the absolute url manually from the request object
         * when redirecting.
         * 
         * For contract, see http://java.sun.com/javaee/5/docs/api/javax/servlet/http/HttpServletResponse.html#sendRedirect(java.lang.String)
         */
        @Override
        public void sendRedirect(String location) throws IOException
        {
            if (ABSOLUTE_URL_PATTERN.matcher(location).matches()) //absolute url
            {
                super.sendRedirect(location);
            }
            else if (location.startsWith("/")) //url relative to server root
            {
                super.sendRedirect(getBaseURL() + location);
            }
            else //url relative to current request URI
            {
                super.sendRedirect(request.getRequestURL() + location);
            }
        }

        private String getBaseURL()
        {
            StringBuffer requestURL = request.getRequestURL();
            String requestURI = request.getRequestURI();
            assert requestURL.toString().endsWith(requestURI) : requestURL + " doesn't end with " + requestURI;
            requestURL.setLength(requestURL.length() - requestURI.length());
            return requestURL.toString();
        }
    }
   

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException
    {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse)
        {
            if (shouldSimulateHttps(request))
            {
                SslRequest sslRequest = new SslRequest((HttpServletRequest) request);
                filterChain.doFilter(sslRequest, new SslResponse((HttpServletResponse)response, sslRequest));
            }
            else
            {
                filterChain.doFilter(request, response);
            }
        }
    }

    private boolean shouldSimulateHttps(ServletRequest request)
    {
        //currently, all staff2.ccci.org requests are redirected to https, so we just check the serverName attribute
        //TODO: better way to handle this; hopefully a bigip header or somesuch.
        String serverName = request.getServerName();
        log.debug("serverName: #0", serverName);
        if (!Strings.isEmpty(serverName) && serverName.contains("staff2.ccci.org"))
        {
            return true; 
        }
        return false;
    }

    public void destroy()
    {
    }

    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

}
