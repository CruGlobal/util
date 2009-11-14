package org.ccci.faces.convert;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.ccci.util.Construction;
import org.ccci.util.Factory;

/**
 * Converts objects using their {@link #toString()} method and factories provided by {@link Construction#getFactory(Class)}. 
 * 
 * @author Matt Drees
 */
public abstract class AbstractValueObjectConverter implements Converter
{

    /**
     * Returns the class of the objects this converter should create in its {@link #getAsObject(FacesContext, UIComponent, String)} method.
     * @param context
     * @param component
     * @return
     */
    protected abstract Class<?> getValueObjectType(FacesContext context, UIComponent component);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value)
    {
        if (value == null || value.isEmpty())
        {
            return null;
        }
        Factory<?> factory = Construction.getFactory(getValueObjectType(context, component));
        try
        {
            return factory.valueOf(value);
        }
        catch (IllegalArgumentException e)
        {
            throw new ConverterException(createMessage(e, context, component));
        }
    }

    /**
     * By default, this class just creates a message from the exception's
     * {@link IllegalArgumentException#getMessage()} value. Subclasses can provide more useful messages (E.g. by
     * using the label for the component)
     */
    protected FacesMessage createMessage(IllegalArgumentException e, FacesContext context, UIComponent component)
    {
        return new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
    }


    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value)
    {
        return value == null ? "" : value.toString();
    }

}
