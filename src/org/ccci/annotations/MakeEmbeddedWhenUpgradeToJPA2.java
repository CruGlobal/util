package org.ccci.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import org.ccci.model.EmployeeId;

/**
 * In JPA1, embedded objects can't be nested.  So, sometimes we have to use
 * straight Strings in composite keys when we'd rather use {@link EmployeeId}s.
 * 
 * We'll want to fix this when we upgrade to JPA2
 * @author Matt Drees
 */
@Documented
@Target(ElementType.FIELD)
public @interface MakeEmbeddedWhenUpgradeToJPA2 {

}
