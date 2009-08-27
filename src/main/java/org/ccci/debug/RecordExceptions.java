package org.ccci.debug;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.seam.annotations.intercept.Interceptors;

/**
 * Record exceptions thrown from the annotated Seam component in the {@link ExceptionContext}, 
 * in order to detect swallowed exceptions.
 * @author Matt Drees
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Interceptors(ExceptionRecordingInterceptor.class)
public @interface RecordExceptions {

}
