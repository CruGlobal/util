package org.ccci.faces.convert;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.ccci.util.Types;

/**
 * Useful for http query param conversion
 * 
 * @author Matt Drees
 *
 * @param <T> must be made concrete by subclass - identifies what type of objects to create
 */
public class ComponentlessValueObjectConverter<T> extends AbstractValueObjectConverter
{

    @Override
    protected Class<T> getValueObjectType(FacesContext context, UIComponent component)
    {
        return Types.getTemplateParameterType(getClass());
    }

}
