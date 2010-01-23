package org.ccci.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated type is threadsafe.  That is, it can be accessed concurrently from multiple threads
 * simultaneously (and without external synchronization) without introducing erroneous behavior. 
 * 
 * @author Matt Drees
 *
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadSafe
{

}
