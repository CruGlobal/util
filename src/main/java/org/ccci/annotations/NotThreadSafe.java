package org.ccci.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated type is not thread-safe.  See {@link ThreadSafe} for a definition of threadsafety.
 * 
 * @author Matt Drees
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface NotThreadSafe
{

}
