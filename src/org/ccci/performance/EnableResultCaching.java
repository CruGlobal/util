package org.ccci.performance;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.seam.annotations.intercept.Interceptors;


/**
 * Signifies that the annotated class has some methods whose results are cached.
 * See {@link MethodResultCache} for cache details.
 * @author Matt Drees
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Interceptors(ResultCachingInterceptor.class)
public @interface EnableResultCaching 
{
}
