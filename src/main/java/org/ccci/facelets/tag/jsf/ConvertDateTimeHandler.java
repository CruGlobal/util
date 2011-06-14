package org.ccci.facelets.tag.jsf;

import javax.faces.view.facelets.ConverterConfig;
import javax.faces.view.facelets.ConverterHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRuleset;
import javax.faces.view.facelets.TagAttribute;

import org.ccci.faces.convert.DateTimeConverter;
import org.joda.time.DateTimeZone;

/**
 * By default, you can't set valueExpressions on converters. So, this allows it for timeZones; the reason is
 * sometimes the timezone changes (in eTimesheet, anyway), potentially during the lifespan of a converter. This
 * allows the converter to query the timezone each time it needs to use it, instead of having to use the timezone
 * that was set at the time the converter was constructed.
 * 
 * @author Matt Drees
 * 
 */
public class ConvertDateTimeHandler extends ConverterHandler
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
        @SuppressWarnings("rawtypes") Class type)
    {
        return super.createMetaRuleset(type).ignore("timeZoneExpression");
    }
    
    @Override
    public void setAttributes(FaceletContext ctx, Object instance)
    {
        super.setAttributes(ctx, instance);
        DateTimeConverter dateTimeConverter = (DateTimeConverter) instance;
        if (timeZoneExpression != null)
        {
            dateTimeConverter.setTimeZoneExpression(timeZoneExpression.getValueExpression(ctx, DateTimeZone.class));
        }
    }

}
