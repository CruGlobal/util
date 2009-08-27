package org.ccci.faces.convert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.ccci.util.Construction;
import org.ccci.util.Exceptions;


public class ValueObjectConverter implements Converter
{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value)
    {
        if (value == null || value.isEmpty())
        {
            return null;
        }
        Class<?> type = ValueExpressionHelper.getValueType(context, component);
        Method constructorMethod = Construction.getConstructionMethod(type);
        try
        {
            return constructorMethod.invoke(null, value);
        }
        catch (IllegalAccessException e)
        {
            throw Exceptions.wrap(e);
        }
        catch (InvocationTargetException e)
        {
            throw Exceptions.wrap(e);
        }
        catch (IllegalArgumentException e)
        {
            throw new ConverterException(e.getMessage(), e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value)
    {
        return value == null ? "" : value.toString();
    }

}
