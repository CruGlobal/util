package org.ccci.testutils.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be added to a persistence context reference in a JPA test.
 * If this is present, then each test method must start a transaction if it intends
 * to do JPA operations, and it must either commit or rollback the transaction at
 * the end of the test.
 * 
 * @see PersistenceContextInjectionListener
 * @author Matt Drees
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManualTransactionControl
{

}
