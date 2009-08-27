package org.ccci.performance;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.intercept.Interceptor;
import org.jboss.seam.core.MethodContextInterceptor;
import org.jboss.seam.intercept.AbstractInterceptor;
import org.jboss.seam.intercept.InvocationContext;

/**
 * Caches method results until either the Invoke Application phase starts, or the request ends.
 * See {@link MethodResultCache}
 * @author Matt Drees
 *
 */
@Interceptor(around = MethodContextInterceptor.class, stateless = true)
public class ResultCachingInterceptor extends AbstractInterceptor 
{

	private static final long serialVersionUID = 1L;

	@Override
	public void setComponent(Component component) {
		super.setComponent(component);
		//TODO: check ResultCached methods are getter-style accessors
	}
	
	@Override
	public Object aroundInvoke(InvocationContext ic) throws Exception {
		if (!ic.getMethod().isAnnotationPresent(ResultCached.class))
		{
			return ic.proceed();
		}
		MethodResultCache cache = MethodResultCache.instance();
		if (cache == null)
		{
			return ic.proceed();
		} 
		else 
		{
			return cache.handleInvocation(ic);
		}
	}

}
