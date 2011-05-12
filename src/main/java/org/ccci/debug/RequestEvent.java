package org.ccci.debug;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ccci.annotations.Immutable;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.joda.time.DateTime;

import com.google.common.base.Throwables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * An immutable snapshot of a request, intended for debugging purposes.
 * 
 * @author Matt Drees
 *
 */
@Immutable
public class RequestEvent implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String serverHost;
    private final String serverIpAddress;
    private final String requestedUrl;
    private final String method;
    private final String referrer;
    private final DateTime occurredAt;
    private final Multimap<String, String> queryParameters = LinkedHashMultimap.create();
    private final Multimap<String, String> postParameters = LinkedHashMultimap.create();
    private final Map<String, String> httpHeaders = Maps.newLinkedHashMap();
    
    private Log log = Logging.getLog(RequestEvent.class);
    
    public RequestEvent(HttpServletRequest request, ParameterSanitizer sanitizer)
    {
        InetAddress i;
        try
        {
            i = InetAddress.getLocalHost();
        } 
        catch (UnknownHostException e)
        {
            throw Throwables.propagate(e);
        }
        this.serverHost = i.getHostName();
        this.serverIpAddress = i.getHostAddress();
        this.referrer = request.getHeader("referer"/* [sic] */);
        this.method = request.getMethod();
        this.requestedUrl = request.getRequestURL().toString();
        this.occurredAt = new DateTime();
        
        //HttpServletRequest does not provide generic API
        @SuppressWarnings("unchecked")
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String param : parameterMap.keySet())
        {
            String queryString = request.getQueryString();
            if (queryString != null && 
                    /* need to verify the param is the 'parameter' part of the query string, and not just a value */
                    (queryString.startsWith(param + "=") || 
                     queryString.contains('&' + param + "=") ||
                     queryString.contains(';' + param + "=") ||  //believe it or not, ';' is a valid query param separator
                     queryString.equals(param) // eg. /soap/MyServiceEndpoint?wsdl
                    )
               )
            {
                this.queryParameters.putAll(param, sanitizer.sanitizeQueryStringParameter(param, Lists.newArrayList(parameterMap.get(param))));
            }
            else
            {
                if ("POST".equals(method))
                {
                    this.postParameters.putAll(param, sanitizer.sanitizePostBodyParameter(param, Lists.newArrayList(parameterMap.get(param))));
                } 
                else
                {
                    log.warn("parameter not in query string in #0 request: #1", method, param);
                }
            }
        }

        //HttpServletRequest does not provide generic API
        @SuppressWarnings("unchecked")
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements())
        {
            String headerName = headerNames.nextElement();
            httpHeaders.put(headerName, request.getHeader(headerName));
        }
    }

    public DateTime getOccurredAt()
    {
        return occurredAt;
    }



    public String getServerHost()
    {
        return serverHost;
    }

    public String getServerIpAddress()
    {
        return serverIpAddress;
    }

    public String getRequestedUrl()
    {
        return requestedUrl;
    }

    public String getMethod()
    {
        return method;
    }

    public String getReferrer()
    {
        return referrer;
    }

    public Multimap<String, String> getQueryParameters()
    {
        return queryParameters;
    }

    public Multimap<String, String> getPostParameters()
    {
        return postParameters;
    }

    public Map<String, String> getHttpHeaders()
    {
        return httpHeaders;
    }
    
}
