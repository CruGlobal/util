package org.ccci.debug;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseListener;

@SuppressWarnings("deprecation")
public class ExceptionRecordingViewRoot extends UIViewRoot
{
    
    private final UIViewRoot delegate;

    public ExceptionRecordingViewRoot(UIViewRoot delegate)
    {
        this.delegate = delegate;
    }


    public void addPhaseListener(PhaseListener newPhaseListener)
    {
        delegate.addPhaseListener(new ExceptionRecordingPaseListenerWrapper(newPhaseListener));
    }

    public void broadcast(FacesEvent event) throws AbortProcessingException
    {
        delegate.broadcast(event);
    }

    public String createUniqueId()
    {
        return delegate.createUniqueId();
    }

    public void decode(FacesContext context)
    {
        delegate.decode(context);
    }

    public void encodeAll(FacesContext context) throws IOException
    {
        delegate.encodeAll(context);
    }


    public void encodeBegin(FacesContext context) throws IOException
    {
        delegate.encodeBegin(context);
    }

    public void encodeChildren(FacesContext context) throws IOException
    {
        delegate.encodeChildren(context);
    }

    public void encodeEnd(FacesContext context) throws IOException
    {
        delegate.encodeEnd(context);
    }

    public UIComponent findComponent(String expr)
    {
        return delegate.findComponent(expr);
    }


    public MethodExpression getAfterPhaseListener()
    {
        return delegate.getAfterPhaseListener();
    }

    public Map<String, Object> getAttributes()
    {
        return delegate.getAttributes();
    }


    public MethodExpression getBeforePhaseListener()
    {
        return delegate.getBeforePhaseListener();
    }

    public int getChildCount()
    {
        return delegate.getChildCount();
    }

    public List<UIComponent> getChildren()
    {
        return delegate.getChildren();
    }

    public String getClientId(FacesContext context)
    {
        return delegate.getClientId(context);
    }
    
    public String getContainerClientId(FacesContext context)
    {
        return delegate.getContainerClientId(context);
    }

    public UIComponent getFacet(String name)
    {
        return delegate.getFacet(name);
    }

    public int getFacetCount()
    {
        return delegate.getFacetCount();
    }

    public Map<String, UIComponent> getFacets()
    {
        return delegate.getFacets();
    }

    public Iterator<UIComponent> getFacetsAndChildren()
    {
        return delegate.getFacetsAndChildren();
    }

    public String getFamily()
    {
        return delegate.getFamily();
    }

    public String getId()
    {
        return delegate.getId();
    }

    public Locale getLocale()
    {
        return delegate.getLocale();
    }

    public UIComponent getParent()
    {
        return delegate.getParent();
    }

    public String getRendererType()
    {
        return delegate.getRendererType();
    }

    public String getRenderKitId()
    {
        return delegate.getRenderKitId();
    }

    public boolean getRendersChildren()
    {
        return delegate.getRendersChildren();
    }

    public ValueBinding getValueBinding(String name)
    {
        return delegate.getValueBinding(name);
    }

    public ValueExpression getValueExpression(String name)
    {
        return delegate.getValueExpression(name);
    }

    public String getViewId()
    {
        return delegate.getViewId();
    }
    
    public boolean invokeOnComponent(FacesContext context, String clientId, ContextCallback callback)
            throws FacesException
    {
        return delegate.invokeOnComponent(context, clientId, callback);
    }

    public boolean isRendered()
    {
        return delegate.isRendered();
    }

    public boolean isTransient()
    {
        return delegate.isTransient();
    }

    public void processApplication(FacesContext context)
    {
        delegate.processApplication(context);
    }

    public void processDecodes(FacesContext context)
    {
        delegate.processDecodes(context);
    }

    public void processRestoreState(FacesContext context, Object state)
    {
        delegate.processRestoreState(context, state);
    }

    public Object processSaveState(FacesContext context)
    {
        return delegate.processSaveState(context);
    }

    public void processUpdates(FacesContext context)
    {
        delegate.processUpdates(context);
    }

    public void processValidators(FacesContext context)
    {
        delegate.processValidators(context);
    }

    public void queueEvent(FacesEvent event)
    {
        delegate.queueEvent(event);
    }

    public void removePhaseListener(PhaseListener toRemove)
    {
        delegate.removePhaseListener(new ExceptionRecordingPaseListenerWrapper(toRemove));
    }

    public void restoreState(FacesContext context, Object state)
    {
        delegate.restoreState(context, state);
    }

    public Object saveState(FacesContext context)
    {
        return delegate.saveState(context);
    }

    public void setAfterPhaseListener(MethodExpression newAfterPhase)
    {
        delegate.setAfterPhaseListener(newAfterPhase);
    }

    public void setBeforePhaseListener(MethodExpression newBeforePhase)
    {
        delegate.setBeforePhaseListener(newBeforePhase);
    }

    public void setId(String id)
    {
        delegate.setId(id);
    }

    public void setLocale(Locale locale)
    {
        delegate.setLocale(locale);
    }

    public void setParent(UIComponent parent)
    {
        delegate.setParent(parent);
    }

    public void setRendered(boolean rendered)
    {
        delegate.setRendered(rendered);
    }

    public void setRendererType(String rendererType)
    {
        delegate.setRendererType(rendererType);
    }

    public void setRenderKitId(String renderKitId)
    {
        delegate.setRenderKitId(renderKitId);
    }

    public void setTransient(boolean transientFlag)
    {
        delegate.setTransient(transientFlag);
    }

    public void setValueBinding(String name, ValueBinding binding)
    {
        delegate.setValueBinding(name, binding);
    }

    public void setValueExpression(String name, ValueExpression binding)
    {
        delegate.setValueExpression(name, binding);
    }

    public void setViewId(String viewId)
    {
        delegate.setViewId(viewId);
    }

    public String toString()
    {
        return "ExceptionRecordingViewRoot[" + delegate.toString() +"]";
    }

    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj instanceof ExceptionRecordingViewRoot == false) return false;
        return delegate.equals(((ExceptionRecordingViewRoot) obj).delegate);
    }

    public int hashCode()
    {
        return delegate.hashCode();
    }

    public static UIViewRoot wrap(UIViewRoot viewRoot)
    {
        return new ExceptionRecordingViewRoot(viewRoot);
    }

}
