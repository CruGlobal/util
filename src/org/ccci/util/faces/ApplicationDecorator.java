package org.ccci.util.faces;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.el.ELContextListener;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;

@SuppressWarnings("deprecation")
public class ApplicationDecorator extends Application
{

    private final Application application;

    public ApplicationDecorator(Application application)
    {
        this.application = application;
    }

    @Override
    public void addComponent(String componentType, String componentClass)
    {
        application.addComponent(componentType, componentClass);
    }

    @Override
    public void addConverter(String converterId, String converterClass)
    {
        application.addConverter(converterId, converterClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addConverter(Class targetClass, String converterClass)
    {
        application.addConverter(targetClass, converterClass);
    }

    @Override
    public void addValidator(String validatorId, String validatorClass)
    {
        application.addValidator(validatorId, validatorClass);
    }

    public void addELContextListener(ELContextListener listener)
    {
        application.addELContextListener(listener);
    }

    public void addELResolver(ELResolver resolver)
    {
        application.addELResolver(resolver);
    }

    public UIComponent createComponent(String componentType) throws FacesException
    {
        return application.createComponent(componentType);
    }

    public UIComponent createComponent(ValueBinding componentBinding, FacesContext context, String componentType)
            throws FacesException
    {
        return application.createComponent(componentBinding, context, componentType);
    }

    public UIComponent createComponent(ValueExpression componentExpression, FacesContext context, String componentType)
            throws FacesException
    {
        return application.createComponent(componentExpression, context, componentType);
    }

    @SuppressWarnings("unchecked")
    public Converter createConverter(Class targetClass)
    {
        return application.createConverter(targetClass);
    }

    public Converter createConverter(String converterId)
    {
        return application.createConverter(converterId);
    }

    @SuppressWarnings("unchecked")
    public MethodBinding createMethodBinding(String ref, Class[] params) throws ReferenceSyntaxException
    {
        return application.createMethodBinding(ref, params);
    }

    public Validator createValidator(String validatorId) throws FacesException
    {
        return application.createValidator(validatorId);
    }

    public ValueBinding createValueBinding(String ref) throws ReferenceSyntaxException
    {
        return application.createValueBinding(ref);
    }

    public boolean equals(Object obj)
    {
        return application.equals(obj);
    }

    @SuppressWarnings("unchecked")
    public Object evaluateExpressionGet(FacesContext context, String expression, Class expectedType) throws ELException
    {
        return application.evaluateExpressionGet(context, expression, expectedType);
    }

    public ActionListener getActionListener()
    {
        return application.getActionListener();
    }

    public Iterator<String> getComponentTypes()
    {
        return application.getComponentTypes();
    }

    public Iterator<String> getConverterIds()
    {
        return application.getConverterIds();
    }

    @SuppressWarnings("unchecked")
    public Iterator<Class> getConverterTypes()
    {
        return application.getConverterTypes();
    }

    public Locale getDefaultLocale()
    {
        return application.getDefaultLocale();
    }

    public String getDefaultRenderKitId()
    {
        return application.getDefaultRenderKitId();
    }

    public ELContextListener[] getELContextListeners()
    {
        return application.getELContextListeners();
    }

    public ELResolver getELResolver()
    {
        return application.getELResolver();
    }

    public ExpressionFactory getExpressionFactory()
    {
        return application.getExpressionFactory();
    }

    public String getMessageBundle()
    {
        return application.getMessageBundle();
    }

    public NavigationHandler getNavigationHandler()
    {
        return application.getNavigationHandler();
    }

    public PropertyResolver getPropertyResolver()
    {
        return application.getPropertyResolver();
    }

    public ResourceBundle getResourceBundle(FacesContext ctx, String name)
    {
        return application.getResourceBundle(ctx, name);
    }

    public StateManager getStateManager()
    {
        return application.getStateManager();
    }

    public Iterator<Locale> getSupportedLocales()
    {
        return application.getSupportedLocales();
    }

    public Iterator<String> getValidatorIds()
    {
        return application.getValidatorIds();
    }

    public VariableResolver getVariableResolver()
    {
        return application.getVariableResolver();
    }

    public ViewHandler getViewHandler()
    {
        return application.getViewHandler();
    }

    public int hashCode()
    {
        return application.hashCode();
    }

    public void removeELContextListener(ELContextListener listener)
    {
        application.removeELContextListener(listener);
    }

    public void setActionListener(ActionListener listener)
    {
        application.setActionListener(listener);
    }

    public void setDefaultLocale(Locale locale)
    {
        application.setDefaultLocale(locale);
    }

    public void setDefaultRenderKitId(String renderKitId)
    {
        application.setDefaultRenderKitId(renderKitId);
    }

    public void setMessageBundle(String bundle)
    {
        application.setMessageBundle(bundle);
    }

    public void setNavigationHandler(NavigationHandler handler)
    {
        application.setNavigationHandler(handler);
    }

    public void setPropertyResolver(PropertyResolver resolver)
    {
        application.setPropertyResolver(resolver);
    }

    public void setStateManager(StateManager manager)
    {
        application.setStateManager(manager);
    }

    public void setSupportedLocales(Collection<Locale> locales)
    {
        application.setSupportedLocales(locales);
    }

    public void setVariableResolver(VariableResolver resolver)
    {
        application.setVariableResolver(resolver);
    }

    public void setViewHandler(ViewHandler handler)
    {
        application.setViewHandler(handler);
    }

    public String toString()
    {
        return application.toString();
    }
    
}
