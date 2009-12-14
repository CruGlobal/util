package org.ccci.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * See http://cagan327.blogspot.com/2006/05/utf-8-encoding-fix-tomcat-jsp-etc.html 
 * or
 * http://www.jroller.com/mert/entry/utf_8_encoding_with_jsf
 * 
 * @author Matt Drees
 *
 */
public class CharsetEncodingFilter implements Filter
{

    @Override
    public void destroy()
    {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException
    {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

}
