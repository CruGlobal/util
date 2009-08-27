package org.ccci.debug;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.ccci.facelets.FaceletViewHandler;

/**
 * TODO: this isn't used; delete either this or {@link FaceletViewHandler}
 *  
 * @author Matt Drees
 */
public class ExceptionReportingFaceletViewHandler extends com.sun.facelets.FaceletViewHandler
{

    public ExceptionReportingFaceletViewHandler(ViewHandler parent)
    {
        super(parent);
    }

    @Override
    protected void handleRenderException(FacesContext context, Exception e) throws IOException, ELException,
            FacesException
    {
        super.handleRenderException(context, e);
        ExceptionContext.getCurrentInstance().recordHandledException(e, "facelets view handler");
    }
    
    @Override
    protected void buildView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException
    {
        super.buildView(context, viewToRender);
    }
}
