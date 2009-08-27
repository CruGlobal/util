package org.ccci.performance;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method's return value will be cached in the {@link MethodResultCache} until either the Invoke Application phase begins, or the request ends (whichever comes first).
 * See {@link MethodResultCache} for more details. 
 * 
 * The annotated method must be a no-arg non-void method.
 * @author Matt Drees
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResultCached {

}
