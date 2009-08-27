package org.ccci.facelets.tag.jsf;

import org.ccci.faces.convert.DateTimeConverter;
import org.joda.time.DateTimeZone;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ConvertHandler;
import com.sun.facelets.tag.jsf.ConverterConfig;

/**
 * By default, you can't set valueExpressions on converters. So, this allows it for timeZones; the reason is
 * sometimes the timezone changes (in eTimesheet, anyway), potentially during the lifespan of a converter. This
 * allows the converter to query the timezone each time it needs to use it, instead of having to use the timezone
 * that was set at the time the converter was constructed.
 * 
 * @author Matt Drees
 * 
 */
public class ConvertDateTimeHandler extends ConvertHandler
{

    private final TagAttribute timeZoneExpression;
    
    public ConvertDateTimeHandler(ConverterConfig config)
    {
        super(config);
        this.timeZoneExpression = getAttribute("timeZoneExpression");
    }
    
    @Override
    protected MetaRuleset createMetaRuleset(
        //unfortunately, Facelets API uses raw type Class, and so to override the method,
        //we have to use a raw type too
        @SuppressWarnings("unchecked") Class type)
    {
        return super.createMetaRuleset(type).ignore("timeZoneExpression");
    }
    
    @Override
    protected void setAttributes(FaceletContext ctx, Object instance)
    {
        super.setAttributes(ctx, instance);
        DateTimeConverter dateTimeConverter = (DateTimeConverter) instance;
        if (timeZoneExpression != null)
        {
            dateTimeConverter.setTimeZoneExpression(timeZoneExpression.getValueExpression(ctx, DateTimeZone.class));
        }
    }

}
