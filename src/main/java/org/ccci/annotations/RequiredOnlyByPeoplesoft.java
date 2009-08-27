package org.ccci.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * A documentation annotation that signifies the annotated field is a column required by peoplesoft,
 * even though it has no use within this application.
 * 
 * For example, peoplesoft requires a "EFFSEQ" column for child tables, even if there will be at most one child.
 * 
 * @author Matt Drees
 */
@Target(ElementType.FIELD)
@Documented
public @interface RequiredOnlyByPeoplesoft {

}
