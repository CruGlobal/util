package org.ccci.faces.convert;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.ccci.util.strings.Strings;
import org.joda.time.LocalDate;

import com.google.common.base.Preconditions;

/**
 * Converts JodaTime LocalDates.  Useful mostly for Seam page param conversion,
 * since this does not depend on the UIComponent being available, like {@link DateTimeConverter} does.
 * 
 * The pattern defaults to "yyyy-MM-dd", but this can be configured through EL. For example, set up a factory
 * in components.xml such as:
 * 
 * <factory name="localDateConverterPattern" value="MM/dd/yyyy"/>
 * 
 * @author Matt Drees
 *
 */
public class LocalDateConverter implements Converter
{
    org.joda.ext.jsf.converter.DateTimeConverter dateTimeConverter = new org.joda.ext.jsf.converter.DateTimeConverter();

    public static String DEFAULT_PATTERN = "yyyy-MM-dd";

    public LocalDateConverter()
    {
        String pattern = DEFAULT_PATTERN;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null)
        {
            ELContext elContext = facesContext.getELContext();
            String configuredPattern = getConfiguredPattern(facesContext, elContext);
            if (!Strings.isEmpty(configuredPattern))
            {
                pattern = configuredPattern;
            }
        }
        
        dateTimeConverter.setPattern(pattern);
    }

    private String getConfiguredPattern(FacesContext facesContext, ELContext elContext)
    {
        Application application = facesContext.getApplication();
        if (application == null) return null;
        ExpressionFactory expressionFactory = application.getExpressionFactory();
        if (expressionFactory == null) return null;
        return (String) expressionFactory.createValueExpression(elContext, "#{localDateConverterPattern}", String.class).getValue(elContext);
    }
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value)
    {
        component = dummyIfNeeded(component);
        Object dateTime = dateTimeConverter.getAsObject(context, component, value);
        return dateTime == null ? null : new LocalDate(dateTime);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value)
    {
        component = dummyIfNeeded(component);
        if (value == null)
        {
            return dateTimeConverter.getAsString(context, component, value);
        }
        Preconditions.checkArgument(value instanceof LocalDate, "%s is not a LocalDate", value);
        LocalDate localDate = (LocalDate) value;
        return dateTimeConverter.getAsString(context, component, localDate.toDateTimeAtStartOfDay());
    }

    /**
     * so DateTimeConverter.getAsObject() doesn't bomb
     * @param component
     * @return
     */
    private UIComponent dummyIfNeeded(UIComponent component)
    {
        if (component == null)
        {
            component = new UIInput(); 
        }
        return component;
    }

}
