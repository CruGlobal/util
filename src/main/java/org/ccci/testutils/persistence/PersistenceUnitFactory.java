package org.ccci.testutils.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

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
     * Should return a non-null {@link EntityManagerFactory} for the given name.  The
     * name will come directly from the {@link PersistenceContext#unitName() unitName}
     * attribute of the PersistenceContext annotation on the {@link EntityManager} field
     * in the test that is requesting this persistence unit.  The annotation may not
     * specify a name, in which case the empty string will be passed as the unitName.   
     * 
     * The returned EntityManagerFactory will be cached for the duration of the test suite,
     * and will be {@link EntityManagerFactory#close() closed} after the suite is finished.
     * 
     * @param unitName the name of the persistence unit that needs to be created
     * @return a non-null {@link EntityManagerFactory}
     */
    EntityManagerFactory buildEntityManagerFactory(String unitName);
}
