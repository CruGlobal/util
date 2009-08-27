package org.ccci.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * A simple documentation annotation, not used at runtime.  It indicates that the annotated constructor 
 * is the constructor is the one required JPA, and potentially not used by any other java code.
 * @author Matt Drees
 */
@Target(ElementType.CONSTRUCTOR)
@Documented
public @interface JpaConstructor
{

}
