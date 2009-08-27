package org.ccci.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.ccci.util.time.JodaHelper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.MutableDateTime;
import org.joda.time.Partial;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadablePartial;


/**
 * This ELResolver performs two related functions.
 * First, it allows a component value to be bound to a particular single-DateTimeField Partial of a MutableDateTime.
 * So, for instance, you could bind an <tt>h:selectOneMenu</tt> to <tt>#{myBackingBean.myDateTime.hourOfDay}</tt>. 
 * Valid DateTimeField names can be found in {@link JodaHelper#DATE_TIME_FIELD_TYPE_NAME_TO_DATE_TIME_FIELD}.
 * This resolver may be used for immutable dates, provided the control is read-only.
 * 
 * Second, this resolver allows a component value to be bound to a "significant portion" of a MutableDateTime.  
 * So, for instance, you could bind an <tt>h:selectOneMenu</tt> to 
 * <tt>#{myBackingBean.myDateTime.hourOfDayRoundFloor}</tt>.
 * When reading, this would return a DateTime that is similar to the given date, but rounded down to the
 * nearest hour.  When writing, myDateTime is modified according to the incoming DateTime.  For each 
 * DateTimeField larger than "hourOfDay", the field values from the incoming DateTime are used; for all
 * others, the field value from myDateTime is used.  The result is a DateTime such that if it were to be
 * roundFloored again, the result would be equal to the incoming DateTime. 
 * This is useful for situations where you'd like to represent a specific hour with a specific
 * time zone.  See JodaTimeELResolverTest for a concrete example.
 * 
 * 
 * The motivation for this resolver is the simplification of the backing bean.  Without something like this, 
 * your backing bean would require multiple properties (hour, minute, etc) to which your jsf controls may be 
 * bound to.  In addition, your action method would need to glue these fields together to form a DateTime.
 * 
 * @author Matt Drees
 */
public class JodaTimeELResolver extends ELResolver
{
    private String roundType = "RoundFloor";

    @Override
    public Class<?> getCommonPropertyType(ELContext elContext, Object base)
    {
        return null;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object base)
    {
        return null;
    }

    
    @Override
    public Class<?> getType(ELContext elContext, Object base, Object property)
    {
        if (base instanceof ReadableDateTime
                && JodaHelper.DATE_TIME_FIELD_TYPE_NAME_TO_DATE_TIME_FIELD.containsKey(property))
        {
            elContext.setPropertyResolved(true);
            return Partial.class;
        }

        if (base instanceof ReadableDateTime && property != null)
        {
            String propertyAsString = property.toString();
            if (propertyAsString.endsWith(roundType))
            {
                String fieldTypeName = propertyAsString.substring(0, propertyAsString.length() - roundType.length());

                if (JodaHelper.DATE_TIME_FIELD_TYPE_NAME_TO_DATE_TIME_FIELD.containsKey(fieldTypeName))
                {
                    elContext.setPropertyResolved(true);
                    return DateTime.class;
                }
            }
        }

        return null;
    }

    /**
     * Resolve #{dateTime.minuteOfHour} and also #{dateTime.hourOfDayRoundFloor}
     */
    @Override
    public Object getValue(ELContext elContext, Object base, Object property)
    {
        if (base instanceof ReadableDateTime 
                && JodaHelper.DATE_TIME_FIELD_TYPE_NAME_TO_DATE_TIME_FIELD.containsKey(property))
        {
            DateTime dateTime = ((ReadableDateTime)base).toDateTime();
            DateTimeFieldType dateTimeFieldType = JodaHelper.DATE_TIME_FIELD_TYPE_NAME_TO_DATE_TIME_FIELD.get(property);
            int value = dateTime.get(dateTimeFieldType);
            elContext.setPropertyResolved(true);
            return new Partial(dateTimeFieldType, value);
        }
        
        if (base instanceof ReadableDateTime && property != null)
        {
            String propertyAsString = property.toString();
            if (propertyAsString.endsWith(roundType))
            {
                String fieldTypeName =
                        propertyAsString.substring(0, propertyAsString.length() - roundType.length());

                if (JodaHelper.DATE_TIME_FIELD_TYPE_NAME_TO_DATE_TIME_FIELD.containsKey(fieldTypeName))
                {
                    DateTime dateTime = ((ReadableDateTime)base).toDateTime();
                    DateTimeFieldType fieldType = JodaHelper.DATE_TIME_FIELD_TYPE_NAME_TO_DATE_TIME_FIELD.get(fieldTypeName);
                    DateTime dateTimeRounded = dateTime.property(fieldType).roundFloorCopy();
                    elContext.setPropertyResolved(true);
                    return dateTimeRounded;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isReadOnly(ELContext elContext, Object base, Object property)
    {
        if (base instanceof DateTime)
            return true;
        return false;
    }

    @Override
    public void setValue(ELContext elContext, Object base, Object property, Object value)
    {
        if (base instanceof MutableDateTime
                && JodaHelper.DATE_TIME_FIELD_TYPE_NAME_TO_DATE_TIME_FIELD.keySet().contains(property)
                && isValidPartial(value, property))
        {
            MutableDateTime mutableDateTime = (MutableDateTime) base;
            ReadablePartial partial = (ReadablePartial) value;
            mutableDateTime.set(partial.getFieldType(0), partial.getValue(0));
            elContext.setPropertyResolved(true);
        }
        

        if (base instanceof MutableDateTime && value instanceof ReadableDateTime && property != null)
        {
            String propertyAsString = property.toString();
            if (propertyAsString.endsWith(roundType))
            {
                String fieldTypeName =
                        propertyAsString.substring(0, propertyAsString.length() - roundType.length());

                if (JodaHelper.DATE_TIME_FIELD_TYPE_NAME_TO_DATE_TIME_FIELD.containsKey(fieldTypeName))
                {
                    MutableDateTime baseAsMutableDateTime = (MutableDateTime) base;
                    DateTime partialDateTime = ((ReadableDateTime) value).toDateTime();
                    DateTimeFieldType fieldType =
                            JodaHelper.DATE_TIME_FIELD_TYPE_NAME_TO_DATE_TIME_FIELD.get(fieldTypeName);
                    
                    long remainder =
                            baseAsMutableDateTime.property(fieldType).getField().remainder(baseAsMutableDateTime.getMillis());
                    DateTime combined = partialDateTime.plus(remainder);
                    baseAsMutableDateTime.setMillis(combined);
                    elContext.setPropertyResolved(true);
                }
            }
        }
        
    }

    private boolean isValidPartial(Object value, Object property)
    {
        if (value instanceof ReadablePartial == false)
        {
            return false;
        }
        ReadablePartial partial = (ReadablePartial) value;
        if (partial.size() != 1)
        {
            return false;
        }
        if (!JodaHelper.DATE_TIME_FIELD_TYPE_NAME_TO_DATE_TIME_FIELD.get(property).equals(partial.getFieldType(0)))
        {
            return false;
        }
        return true;
    }

}
