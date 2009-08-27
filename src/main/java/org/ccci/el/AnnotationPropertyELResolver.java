package org.ccci.el;

import java.beans.FeatureDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.ccci.util.Properties;
import org.jboss.seam.util.Reflections;
/**
 * An el resolver that can resolve annotations attached to properties, 
 * and properties of annotations.  E.g.
 * <pre>
 * #{foo.annotationsFor['name']['Length'].max}
 * </pre>
 * for something like
 * <pre>
 * &#64;Name("foo)
 * public class Foo {
 *   &#64;Length(max = 20)
 *   private String name
 * }
 * </pre>
 * 
 * Field, getter, and setter annotations are found.  Annotations can be specified by
 * the annotation class's simple name or fully qualified name.
 * If more than one annotation is found for the given name, an exception is thrown.
 * If the named annotation isn't found, null is returned.
 * <br/>
 * 
 * Depends on Seam utilities.  Thanks Seam.
 * @author matt.drees
 *
 */
public class AnnotationPropertyELResolver extends ELResolver {


	@Override
	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		return null;
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context,
			Object base) {
		return null;
	}

	@Override
	public Class<?> getType(ELContext context, Object base, Object property){
		return null;
	}

	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) {
		return true;
	}

	@Override
	public void setValue(ELContext context, Object base, Object property, Object value) {
		return;
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property) {
		if (base == null) {
			return null;
		}
		
		if ("annotationsFor".equals(property)){
			context.setPropertyResolved(true);
			return new PropertyResolver(base);
		}
		
		if (property == null) {
			return null;
		}
		
		if (base instanceof Resolver && property instanceof String) {
			Resolver resolver = (Resolver) base;
			String propertyAsString = (String) property;
			context.setPropertyResolved(true);
			return resolver.resolve(propertyAsString);
		}
	
		if (base instanceof Annotation && property instanceof String) {
			Annotation annotation = (Annotation) base;
			String propertyAsString = (String) property;
			Method method = Reflections.getMethod(annotation, propertyAsString);
			if (method == null) {
				throw new IllegalArgumentException(String.format(
					"Annotation %s does not have a property named %s",
					annotation.getClass().getName(), propertyAsString));
			}
			context.setPropertyResolved(true);
			return Reflections.invokeAndWrap(method, annotation, new Object[]{});
		}
		
		return null;
	}
	
	private interface Resolver {
		public Object resolve(String propertyToResolve);
	}
	
	private static class PropertyResolver implements Resolver {
		private final Object base;
		
		public PropertyResolver(final Object base) {
			this.base = base;
		}
		
		public Object resolve(String propertyToResolve) {
			return new AnnotationResolver(base, propertyToResolve);
		}
	}
	
	private static class AnnotationResolver implements Resolver {
		private final Object base;
		private final String property;
		
		private AnnotationResolver(final Object base, final String property) {
			this.base = base;
			this.property = property;
		}

		public Object resolve(String propertyToResolve) {
			return getAnnotation(propertyToResolve);
		}

		private Annotation getAnnotation(String name) {
			if (property == null) {
				throw new IllegalStateException("must set property first");
			}
			Class<?> baseClass = base.getClass();
			
			Set<Annotation> annotations = Properties.getAllAnnotationsForProperty(baseClass, property);
			
			Annotation found = null;
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().getSimpleName().equals(name) ||
						annotation.annotationType().getName().equals(name)) {
					if (found != null) {
						throw new IllegalArgumentException(String.format(
							"More than one annotation named %s found for %s.%s; one is %s, the other is %s",
							name, baseClass.getSimpleName(), property, found, annotation));
					}
					found = annotation;
				}
			}
			return found;
		}
	}
}
