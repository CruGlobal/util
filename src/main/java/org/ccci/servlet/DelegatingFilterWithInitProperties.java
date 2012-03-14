package org.ccci.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.ccci.util.NkUtil;
import org.ccci.util.properties.PropertiesWithFallback;

/**
 * This is a wrapper servlet that requires "delegate" init-param specifying the
 * class of the servlet to which it will delegate. This class enables
 * parametrized init-param's in the web.xml in the form ${param-name} and
 * dereferences them in System properties and provides the translated version to
 * the delegate. e.g. if you have set System property "mypath=/a/b/c" and you
 * use this servlet with an init-param tag such as
 * 
 * <pre>
 * <code>
 *         <init-param>
 *          <param-name>path</param-name>
 *          <param-value>${mypath}</param-value>
 *      </init-param>
 * </code>
 * </pre>
 * 
 * Then the delegate servlet call to getInitParameter("path") will return
 * "/a/b/c".
 * 
 * Adapted from http://www.coderanch.com/t/79094/Websphere/environment-variable-referance-Web-xml
 * Code by M Simpson 
 */
public class DelegatingFilterWithInitProperties implements Filter
{
    private FilterConfig filterConfigProxy;
    private Filter delegate;
    private Properties props;

    public void init(final FilterConfig filterConfig) throws ServletException
    {

        String propertyFiles = filterConfig.getInitParameter("propertyFiles");
        String[] propertyFilesArray = propertyFiles.split(",");
        for(int i=0; i<propertyFilesArray.length; i++) propertyFilesArray[i] = propertyFilesArray[i].trim();

        if (propertyFilesArray.length==0 || NkUtil.isBlank(propertyFilesArray[0]))
        {
            props = System.getProperties();
        }
        else
        {
            props = new PropertiesWithFallback(false, propertyFilesArray);
        }

        filterConfigProxy = (FilterConfig) Proxy.newProxyInstance(getClass().getClassLoader(),
            new Class[] { FilterConfig.class }, new InvocationHandler()
            {
                public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
                {
                    if ("getInitParameter".equals(method.getName()))
                    {
                        return ParameterSubstituter.dereference((String) method.invoke(filterConfig, args), props);
                    }
                    else
                    {
                        return method.invoke(filterConfig, args);
                    }
                }
            });

        final String delegateServletClassName = filterConfigProxy.getInitParameter("delegate");
        try
        {
            delegate = (Filter) Class.forName(delegateServletClassName).newInstance();
        }
        catch (Exception ex)
        {
            throw new ServletException("Valid classname of delegate HttpServlet subclass required", ex);
        }

        delegate.init(filterConfigProxy);
    }

    @Override
    public void destroy()
    {
        delegate.destroy();
    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException,
            ServletException
    {
        delegate.doFilter(arg0, arg1, arg2);
    }

}
