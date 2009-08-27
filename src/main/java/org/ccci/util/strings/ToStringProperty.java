package org.ccci.util.strings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to indicate a field in a model object is a useful field
 * for identifying that object.  Like a primary key.
 * Used by ToStringBuilder.  Thus, if you have an attribute annotated with this, you should
 * implement {@link Object#toString()} by calling {@code new ToStringBuilder(this).toString()}. 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ToStringProperty {
    
}
