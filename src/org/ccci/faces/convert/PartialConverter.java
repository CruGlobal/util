package org.ccci.faces.convert;


import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.ccci.util.strings.Strings;
import org.ccci.util.time.JodaHelper;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Partial;
import org.joda.time.ReadablePartial;

import com.google.common.base.Preconditions;

/**
 * Intended mostly for situations where the string output is not displayed, as in h:selectOneMenu options; 
 * the output will look something like "[hourOfDay=3, minuteOfHour=14, secondOfMinute=15]"
 * 
 * Does not work for Partials with custom DateTimeFieldType implementations.
 * Constructed partials use ISO chronology.
 * 
 * @author Matt Drees
 *
 */
public class PartialConverter implements Converter
{

    /**
     * @param component may be null
     * @param facesContext may be null
     */
    public Object getAsObject(FacesContext facesContext, UIComponent component, String string)
    {
        if (Strings.isEmpty(string))
        {
            return null;
        }
        try
        {
            Preconditions.checkArgument(string.startsWith("[") && string.endsWith("]"),
                "%s is an invalid string representation of a partial", string);
            string = string.substring(1, string.length() - 1);
            StringTokenizer tokenizer = new StringTokenizer(string, ", =");
            
            Partial partial = new Partial();
            
            while (tokenizer.hasMoreTokens())
            {
                String dateTimeFieldTypeName = tokenizer.nextToken();
                DateTimeFieldType fieldType = JodaHelper.DATE_TIME_FIELD_TYPE_NAME_TO_DATE_TIME_FIELD.get(dateTimeFieldTypeName);
                if (fieldType == null)
                {
                    throw new IllegalArgumentException(String.format("No standard DateTimeFieldType named %s", fieldType));
                }
                String fieldValue = tokenizer.nextToken();
                int value;
                try
                {
                    value = Integer.parseInt(fieldValue);
                } 
                catch (NumberFormatException e)
                {
                    throw new IllegalArgumentException(String.format("Bad value (%s) for field %s", fieldValue, fieldType), e);
                }
                partial = partial.with(fieldType, value);
            }
            return partial;
        }
        catch (IllegalArgumentException e)
        {
            throw fail(string, e);
        }
        catch (NoSuchElementException e)
        {
            throw fail(string, e);
        }
    }

    private ConverterException fail(String string, Exception e)
    {
        return new ConverterException(String.format("Cannot convert %s to a Partial", string), e);
    }

    /**
     * @param component may be null
     * @param facesContext may be null
     */
    public String getAsString(FacesContext facesContext, UIComponent component, Object object)
    {
        if (object == null)
        {
            return "";
        }
        Preconditions.checkArgument(object instanceof ReadablePartial, "%s is not a ReadablePartial", object);
        ReadablePartial readablePartial = (ReadablePartial) object;
        Partial partial = new Partial(readablePartial);
        for (DateTimeField field : partial.getFields())
        {
            DateTimeFieldType type = field.getType();
            Preconditions.checkArgument(
                JodaHelper.DATE_TIME_FIELD_TYPE_NAME_TO_DATE_TIME_FIELD.containsKey(type.getName()),
                "Partial %s has a DateTimeField (%s) that is not standard, and cannot be converted",
                readablePartial, field);
        }
        return partial.toStringList();
    }

}
