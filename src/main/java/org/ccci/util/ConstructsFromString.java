package org.ccci.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a static method with one string argument can be used to construct an instance
 * of this class.  Used by ValueObjectConverter.  The method should throw an IllegalArgumentException
 * with a message to be displayed to the user if the string is invalid.
 * 
 * @author Matt Drees
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConstructsFromString {

}
