package org.ccci.debug;

import static org.jboss.seam.ScopeType.STATELESS;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.google.common.base.Throwables;
import org.ccci.util.seam.Components;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.google.common.collect.Lists;

@Name("additionalExceptionDetailsPrinter")
@Scope(STATELESS)
@BypassInterceptors
public class AdditionalExceptionDetailsPrinter
{
    
    public List<String> getAdditionalDetails(Throwable rootThrowable)
    {
        List<String> details = Lists.newArrayList();
        for (Throwable throwable : ExceptionUtil.eachThrowableInChain(rootThrowable)) {
            if (detailsAvailableForThrowable(throwable))
            {
                details.addAll(getDetails(throwable));
            }
        }
        return details;
    }


    public boolean detailsAvailableForThrowable(Throwable throwable)
    {
        BeanInfo info;
        try
        {
            info = Introspector.getBeanInfo(throwable.getClass());
        }
        catch (IntrospectionException e)
        {
            return false;
        }
        return info.getPropertyDescriptors().length != 0;
    }

    public List<String> getDetails(Throwable throwable)
    {
        BeanInfo info;
        try
        {
            info = Introspector.getBeanInfo(throwable.getClass());
        }
        catch (IntrospectionException e)
        {
            throw Throwables.propagate(e);
        }
        PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();

        List<String> details = Lists.newArrayList();
        details.add(throwable.getClass().getSimpleName() + ":");
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors)
        {
            details.add(
                propertyDescriptor.getDisplayName() +
                ": " +
                readProperty(throwable, propertyDescriptor));
        }
        return details;
    }

    private Object readProperty(Throwable throwable, PropertyDescriptor propertyDescriptor)
    {
        Method readMethod = propertyDescriptor.getReadMethod();
        if (readMethod == null)
            return "<cannot be read>";
        try
        {
            return readMethod.invoke(throwable);
        }
        catch (IllegalAccessException e)
        {
            // PropertyDescriptors should not hand out read methods that aren't public,
            // so this should never happen.
            // But since this is error-handling code,
            // I'll be a little more conservative and not throw an AssertionError.
            return unavailable(e);
        }
        catch (InvocationTargetException e)
        {
            return unavailable(e);
        }
    }

    private String unavailable(Exception e)
    {
        return "<unavailable: " + e + " >";
    }

    public static AdditionalExceptionDetailsPrinter instance()
    {
        return Components.getStatelessComponent(AdditionalExceptionDetailsPrinter.class);
    }
    
    
}
