package org.ccci.faces.convert;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Partial;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * Extends JodaExt's {@link org.joda.ext.jsf.converter.DateTimeConverter} to support {@link LocalDate}s,
 * {@link LocalTime}s, and {@link Partial}s in addition to {@link ReadableInstant}s. If neither {@code type} nor
 * {@code pattern} is specified, then {@code type} will be set to "date" for {@code LocalDate}s, "time" for
 * {@code LocalTime}s, and "both" for {@code Partial}s and {@code ReadableInstant}s
 * 
 * This converter uses the {@link UIComponent} argument, so the argument must not be null. A consequence is this
 * converter can't be used for Seam page parameters. Instead, see {@link LocalDateConverter}.
 * 
 * It looses milliseconds.
 * 
 * If converter will be used to create DateTime objects, timeStyle should not be set to "full". The time zone short
 * name isn't parseable by joda.
 * 
 * @author Matt Drees
 * 
 */
public class DateTimeConverter extends org.joda.ext.jsf.converter.DateTimeConverter
{
    private Log log = Logging.getLog(DateTimeConverter.class);

    //copied from superclass.  The punks made them private.
    public static final String TYPE_DATE = "date";
    public static final String TYPE_TIME = "time";
    public static final String TYPE_BOTH = "both";

    public static final String STYLE_DEFAULT = "default";
    public static final String STYLE_MEDIUM = "medium";
    public static final String STYLE_SHORT = "short";
    public static final String STYLE_LONG = "long";
    public static final String STYLE_FULL = "full";
    
    private boolean usingDefaultType = true;
    
    private ValueExpression timeZoneExpression;

    /**
     * used to temporarily store the facesContext during the duration of a getAsString or getAsObject
     */
    transient FacesContext facesContext;
    
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value)
    {
        this.facesContext = facesContext;
        if (value instanceof LocalDate)
        {
            setTypeIfUnspecified(TYPE_DATE);
            if (getPattern() == null && !getType().equals(TYPE_DATE))
            {
                log.warn("value is a LocalDate (#0), but converter type is #1", value, getType());
            }
            LocalDate localDate = (LocalDate) value;
            return super.getAsString(facesContext, uiComponent, localDate.toDateTimeAtStartOfDay(getTimeZone()));
        }
        if (value instanceof LocalTime)
        {
            setTypeIfUnspecified(TYPE_TIME);
            if (getPattern() == null && !getType().equals(TYPE_TIME))
            {
                log.warn("value is a LocalTime (#0), but converter type is #1", value, getType());
            }
            LocalTime localTime = (LocalTime) value;
            return super.getAsString(facesContext, uiComponent, localTime.toDateTimeToday(getTimeZone()));
        }
        if (value instanceof ReadablePartial)
        {
            setTypeIfUnspecified(TYPE_BOTH);
            ReadablePartial readablePartial = (ReadablePartial) value;
            return super.getAsString(facesContext, uiComponent, readablePartial.toDateTime(new DateTime()));
        }
        if (value instanceof ReadableInstant)
        {
            setTypeIfUnspecified(TYPE_BOTH);
        }
        String valueAsString = super.getAsString(facesContext, uiComponent, value);
        this.facesContext = null;
        return valueAsString;
    }

    /**
     * If the user specified a type in xhtml, don't override
     * their choice.  But set the given type if not.  This allows
     * us to do smart defaults based on the type of object being
     * converted.
     * 
     * This is kinda ugly because the superclass directly calls 
     * its private variable instead of getType(), which is overrideable.
     * @param type
     */
    private void setTypeIfUnspecified(String type)
    {
        if (usingDefaultType)
        {
            super.setType(type);
        }
    }
    
    
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value)
    {
        this.facesContext = facesContext;
        Class<?> type = ValueExpressionHelper.getValueType(facesContext, uiComponent, 
            Lists.<Class<?>>newArrayList(DateTime.class, LocalDate.class, LocalTime.class));
        Preconditions.checkArgument(type != null, 
            "DateTimeConverter is not attached to a component bound to either a" +
            " DateTime, LocalDate, or LocalTime.");
        Object valueAsObject;
        if (type.isAssignableFrom(DateTime.class)) 
        {
            setTypeIfUnspecified(TYPE_BOTH);
            valueAsObject = (DateTime) super.getAsObject(facesContext, uiComponent, value);
        }
        else if (type.isAssignableFrom(LocalDate.class)) 
        {
            setTypeIfUnspecified(TYPE_DATE);
            Object dateTime = super.getAsObject(facesContext, uiComponent, value);
            valueAsObject = dateTime == null ? null : new LocalDate(dateTime);
        }
        else if (type.isAssignableFrom(LocalTime.class)) 
        {
            setTypeIfUnspecified(TYPE_TIME);
            Object dateTime = super.getAsObject(facesContext, uiComponent, value);
            valueAsObject = dateTime == null ? null : new LocalTime(dateTime);
        } 
        else
        {
            throw new AssertionError("ValueExpressionHelper.getValueType() broke its contract");
        }
        this.facesContext = null;
        return valueAsObject;
    }

    @Override
    public void setDateStyle(String dateStyle)
    {
        ImmutableSet<String> validStyles = ImmutableSet.of(STYLE_DEFAULT, STYLE_FULL, STYLE_LONG, STYLE_MEDIUM, STYLE_SHORT);
        Preconditions.checkArgument(validStyles.contains(dateStyle), "%s is not one of %s", dateStyle, validStyles);
        super.setDateStyle(dateStyle);
    }
    
    @Override
    public void setType(String type)
    {
        ImmutableSet<String> validTypes = ImmutableSet.of(TYPE_DATE, TYPE_TIME, TYPE_BOTH);
        Preconditions.checkArgument(validTypes.contains(type), "%s is not one of %s", type, validTypes);
        usingDefaultType = false;
        super.setType(type);
    }

    @Override
    public void setPattern(String pattern)
    {
        usingDefaultType = false;
        super.setPattern(pattern);
    }

    @Override
    public DateTimeZone getTimeZone()
    {
        if (timeZoneExpression == null)
        {
            return super.getTimeZone();
        }
        else
        {
            ELContext context;
            if (facesContext == null)
            {
                context = FacesContext.getCurrentInstance().getELContext();
            }
            else
            {
                context = facesContext.getELContext();
            }
            return (DateTimeZone) timeZoneExpression.getValue(context);
        }
    }
    
    /**
     * Needed if the timezone may change during the life of the view's component tree
     */
    public void setTimeZoneExpression(ValueExpression timeZoneExpression)
    {
        this.timeZoneExpression = timeZoneExpression;
    }
    
    @Override
    public Object saveState(FacesContext facesContext)
    {
        Object[] values = new Object[3];
        values[0] = super.saveState(facesContext);
        values[1] = usingDefaultType;
        values[2] = timeZoneExpression;
        return values;
    }

    @Override
    public void restoreState(FacesContext facesContext, Object state)
    {
        Object[] values = (Object[]) state;
        Object superState = values[0];
        usingDefaultType = (Boolean) values[1];
        timeZoneExpression = (ValueExpression) values[2];
        super.restoreState(facesContext, superState);
    }
}
