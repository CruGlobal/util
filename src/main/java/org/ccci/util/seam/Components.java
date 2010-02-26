package org.ccci.util.seam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;

import com.google.common.base.Preconditions;

public class Components {

	public static <T> T lookup(Class<T> componentClass) {
		return componentClass.cast(getInstance(componentClass));
	}
	
	/**
	 * Note: only use if objects don't really have a @destroy method
	 * @param <T>
	 * @param componentClass
	 * @param request
	 * @return
	 */
	public static <T> T getEventScopedComponentOutsideEventContext(Class<T> componentClass, HttpServletRequest request)
	{
	    Object attribute = request.getAttribute(Seam.getComponentName(componentClass));
	    if (componentClass.isInstance(attribute)) 
	    {
	        return componentClass.cast(attribute);
	    }
	    return null;
	}

	/**
	 * Note: only use if objects don't really have a @destroy method
	 * @param <T>
	 * @param componentClass
	 * @param request
	 * @return
	 */
	public static <T> T getSessionScopedComponentOutsideSessionContext(Class<T> componentClass, HttpSession session)
	{
	    Preconditions.checkNotNull(session, "session is null");
	    Preconditions.checkNotNull(componentClass, "componentClass is null");
	    Object attribute = session.getAttribute(Seam.getComponentName(componentClass));
        if (componentClass.isInstance(attribute)) 
        {
            return componentClass.cast(attribute);
        }
        return null;
	}

    /**
     * Not suitable for manager or factory components (those that use @unwrap)
     */
    public static <T> T getStatelessComponent(Class<T> componentClass)
    {
        if (Contexts.isApplicationContextActive())
        {
            return componentClass.cast(Component.getInstance(componentClass));
        }
        Lifecycle.beginCall();
        try
        {
            return componentClass.cast(Component.getInstance(componentClass));
        }
        finally
        {
            Lifecycle.endCall();
        }
    }

	/**
	 * See {@link #getInstance(String)}, except retrieve component name from the {@link Name} annotation on the given class
	 * @param componentClass
	 * @return
	 */
    public static Object getInstance(Class<?> componentClass)
    {
        return getInstance(Component.getComponentName(componentClass));
    }
	
    
    /**
     * Very much like {@link Component#getInstance(Class)}, except create contexts if they don't exist
     * @param componentClass
     * @return
     */
    public static Object getInstance(String componentName)
    {
        if (Contexts.isApplicationContextActive())
        {
            return Component.getInstance(componentName);
        }
        Lifecycle.beginCall();
        try
        {
            return Component.getInstance(componentName);
        }
        finally
        {
            Lifecycle.endCall();
        }
    }
    
}
