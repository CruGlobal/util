package org.ccci.testutils.persistence;

import javax.persistence.EntityManagerFactory;

/**
 * Responsible for creating the {@link EntityManagerFactory} that will be used in JPA tests.
 * 
 * @see PersistenceContextInjectionListener
 * 
 * @author Matt Drees
 */
public interface PersistenceUnitFactory
{
    /**
     * Should return a non-null {@link EntityManagerFactory} for the given {@link PersistenceContextReference}.     
     * 
     * The returned EntityManagerFactory will be cached for the duration of the test suite,
     * and will be {@link EntityManagerFactory#close() closed} after the suite is finished.
     * 
     * @param persistenceContextReference contains the name of the persistence unit that needs to be created, 
     *  and other context information
     * @return a non-null {@link EntityManagerFactory}
     */
    EntityManagerFactory buildEntityManagerFactory(PersistenceContextReference persistenceContextReference);
}
