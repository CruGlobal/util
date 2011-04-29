package org.ccci.util;

import javax.servlet.http.HttpServletRequest;

public class HttpRequests
{

    public static String getFullPath(HttpServletRequest httpRequest)
    {
        String servletPath = httpRequest.getServletPath();
        servletPath = servletPath == null ? "" : servletPath;
        String pathInfo = httpRequest.getPathInfo();
        pathInfo = pathInfo == null ? "" : pathInfo;
        String fullPath = servletPath + pathInfo;
        return fullPath;
    }
}
