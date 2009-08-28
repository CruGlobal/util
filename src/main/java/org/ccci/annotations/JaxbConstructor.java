package org.ccci.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * A document annotation indicating that the annotated constructor (which should take no arguments) exists only for
 * JAXB to use.  Client code should use an alternative constructor.
 * 
 * @author Matt Drees
 * 
 */
@Target(ElementType.CONSTRUCTOR)
@Documented
public @interface JaxbConstructor
{

}
