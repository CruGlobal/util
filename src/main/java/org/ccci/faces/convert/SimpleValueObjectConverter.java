package org.ccci.faces.convert;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * A JSF Converter for simple string-based value objects.  This converter uses the {@link UIComponent} to which it is attached,
 * so it is not useful for Seam request-parameter conversion.
 * 
 * @author Matt Drees
 */
public class SimpleValueObjectConverter extends AbstractValueObjectConverter implements Converter
{

    @Override
    protected Class<?> getValueObjectType(FacesContext context, UIComponent component)
    {
        return ValueExpressionHelper.getValueType(context, component);   
    }
    

    //TODO: override #getMessage().  Maybe grab component's label.

}
