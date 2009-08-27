package org.ccci.testutils.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.google.common.base.Preconditions;

/**
 * Subclasses should setup a @BeforeClass method that initializes {@code factory}
 * @author Matt Drees
 *
 */
public abstract class PersistenceTest
{

    
    private static EntityManagerFactory factory;
    
    protected EntityManager entityManager;

    public PersistenceTest()
    {
        super();
    }

    /*
     * I've found it best to have only *one* beforeSuite/beforeClass method, and call other methods from there.
     * Otherwise, multiple beforeClass methods can get run in arbitrary order. In particular, sometimes they run in
     * different order depending on whether you're running in Debug mode or not. 
     * -Matt
     */
    @BeforeSuite
    @BeforeClass
    public static void setup()
    {
        setupLogging();
    }
    
    @AfterSuite
    @AfterClass
    public static void cleanup()
    {
        closeEntityManagerFactory();
        cleanupLogging();
    }

    @Before
    @BeforeMethod
    public void setupEntityManager()
    {
        EntityManagerFactory factory = getFactory();
        Preconditions.checkState(factory != null);
        Preconditions.checkState(factory.isOpen());
        entityManager = factory.createEntityManager();
        entityManager.getTransaction().begin();
    }

    @After
    @AfterMethod
    public void cleanupEntityManager()
    {
        if (entityManager != null)
        {
            try
            {
                EntityTransaction transaction = entityManager.getTransaction();
                if (transaction != null && transaction.isActive())
                {
                    transaction.rollback();
                }
            }
            finally
            {
                entityManager.close();
                entityManager = null;
            }
        }
    }
    
    abstract EntityManagerFactory buildEntityManagerFactory();
    
    /*
     * Construct EntityManagerFactory lazily.  I forget the reason why, but it was important.
     */
    public EntityManagerFactory getFactory()
    {
        if (factory == null)
        {
            factory = buildEntityManagerFactory();
        }
        return factory;
    }


    private static void closeEntityManagerFactory()
    {
        if (factory != null)
        {
            factory.close();
            factory = null;
        }
    }
    
    protected String getPersistenceUnitName()
    {
        UsesPersistenceUnit usesPersistenceUnit = this.getClass().getAnnotation(UsesPersistenceUnit.class);
        if (usesPersistenceUnit != null)
        {
            return usesPersistenceUnit.value();
        }
        return getDefaultPersistenceUnitName();
    }


    abstract protected String getDefaultPersistenceUnitName();
    

    public static void setupLogging()
    {
        BasicConfigurator.configure(); 
        Logger.getLogger("org.hibernate").setLevel(Level.WARN);
    }
    
    public static void cleanupLogging()
    {
        BasicConfigurator.resetConfiguration();
    }
}
