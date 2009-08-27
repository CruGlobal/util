package org.ccci.facelets;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;

import org.ccci.debug.ExceptionContext;
import org.ccci.debug.RecordedExceptions;

/**
 * Integrates with exception handling
 * @author Matt Drees
 *
 */
public class FaceletViewHandler extends com.sun.facelets.FaceletViewHandler
{

    public FaceletViewHandler(ViewHandler parent)
    {
        super(parent);
    }

    @Override
    protected void handleRenderException(FacesContext context, Exception e) throws IOException, ELException,
            FacesException
    {
        super.handleRenderException(context, e);
        RecordedExceptions.instance().recordHandledException(e);
        ExceptionContext.getCurrentInstance().recordHandledException(e, "facelets view handler");
    }
    
    //not sure why yet, but these break Mojarra
// 
//    @Override
//    public UIViewRoot createView(FacesContext context, String viewId)
//    {
//        return ExceptionRecordingViewRoot.wrap(super.createView(context, viewId));
//    }
//
//    @Override
//    public UIViewRoot restoreView(FacesContext context, String viewId)
//    {
//        return ExceptionRecordingViewRoot.wrap(super.restoreView(context, viewId));
//    }
}
