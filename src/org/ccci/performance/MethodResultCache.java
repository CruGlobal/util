package org.ccci.performance;

import java.lang.reflect.Method;
import java.util.Map;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import org.ccci.util.SimpleValueObject;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.intercept.InvocationContext;

import com.google.common.collect.Maps;

/**
 * An request-scoped cache for holding the results of methods that are annotated @ResultCached.
 * The cache is cleared for the Invoke Application phase, and is not used during that phase, because
 * this is often when values will change.  For simplicity's sake, there's no way to programmatically 
 * invalidate a cached result.
 * 
 * The purpose for this cache is to avoid performance penalties when a backing bean has its methods invoked 
 * many times during a request, and each method invocation triggers an expensive injection process, even though
 * the result hasn't changed since the last time this method was invoked.  
 * 
 * Usage example: <pre><code>
 * 
 * {@literal @Name("timeEntry")}
 * {@literal @EnableResultCaching}
 * public class TimeEntry
 * {
 *    ...
 *    {@literal @ResultCached}
 *    public boolean getHourlyTimesheet() { ... }
 * }
 * </code></pre>
 * 
 * See also wiki page <a href="http://itwiki.ccci.org/confluence/display/ITG/Fixing+performance+problems+related+to+excessive+injection+in+Seam+apps">here</a>.
 * 
 * @author Matt Drees
 *
 */
@Name("methodResultCache")
@Scope(ScopeType.EVENT)
@AutoCreate
@BypassInterceptors
public class MethodResultCache 
{

	private final class Key extends SimpleValueObject
	{
		private Object target;
		private Method method;

		public Key(Object target, Method method) {
			this.target = target;
			this.method = method;
		}

		@Override
		protected Object[] getMembers() {
			return new Object[]{target, method};
		}
	}
	
	private Map<Key, Object> cache = Maps.newHashMap();
	
	private PhaseId currentPhaseId;
	
	public static MethodResultCache instance() {
		return Contexts.isEventContextActive() 
			? (MethodResultCache) Component.getInstance(MethodResultCache.class, ScopeType.EVENT)
			: null;
	}

	@Observer("org.jboss.seam.beforePhase")
	public void beforePhase(PhaseEvent event)
	{
		if (event.getPhaseId() == PhaseId.INVOKE_APPLICATION)
		{
			cache.clear();
		}
		currentPhaseId = event.getPhaseId();
	}
	
	@Observer("org.jboss.seam.afterPhase")
	public void afterPhase(PhaseEvent event)
	{
		currentPhaseId = null;
	}
	
	public Object handleInvocation(InvocationContext ic) throws Exception 
	{
		if (currentPhaseId == null || currentPhaseId == PhaseId.INVOKE_APPLICATION)
		{
			return ic.proceed();
		}
		
		Object cachedResult = get(ic);
		if (cachedResult != null) 
		{
			return cachedResult;
		} 
		else 
		{
			Object invocationResult = ic.proceed();
			put(ic, invocationResult);
			return invocationResult;
		}
	}

	private Object get(InvocationContext ic) {
		return cache.get(new Key(ic.getTarget(), ic.getMethod()));
	}

	private void put(InvocationContext ic, Object invocationResult) {
		cache.put(new Key(ic.getTarget(), ic.getMethod()), invocationResult);
	}
	
}
