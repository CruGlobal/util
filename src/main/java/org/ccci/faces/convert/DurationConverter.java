package org.ccci.faces.convert;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.joda.time.Duration;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import com.google.common.base.Preconditions;

public class DurationConverter implements Converter
{

    private PeriodFormatter formatter = ISOPeriodFormat.standard();

    public Object getAsObject(FacesContext facesContext, UIComponent component, String string)
    {
        try
        {
            return formatter.parsePeriod(string).toStandardDuration();
        }
        catch (IllegalArgumentException e)
        {
            throw new ConverterException(String.format("Cannot convert %s", string), e);
        }
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object object)
    {
        Preconditions.checkArgument(object instanceof Duration, "%s is not a Duration, but a %s", object,
            object.getClass());
        Duration duration = (Duration) object;
        return formatter.print(duration.toPeriod());
    }
}
