package org.ccci.testutils.persistence;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.ccci.util.Annotations;
import org.ccci.util.Classes;
import org.ccci.util.Iterables;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestNGListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * A TestNG listener that enables tests to be written without JPA boilerplate.
 * 
 * To use this listener, you need to register it with TestNG.  The best way I've found to do this is to 
 * use the {@link ServiceLoader} mechanism, as this works well within Eclipse and within Maven.
 * You will need to create a simple file called {@code org.testng.ITestNGListener}, located in
 * {@code /src/test/resources/META-INF/services}.  The file should contain only the text 
 * {@code org.ccci.testutils.persistence.PersistenceContextInjectionListener}. 
 * 
 * Once that's done, your test class should look something like this:
 * 
 * <pre>
 * {@code @}PersistenceUnitBuiltBy(HibernateInMemoryH2PersistenceUnitFactory.class)
 * public class MyTest
 * {
 * 
 *    {@code @}PersistenceContext(unitName = "my-persistence-unit")
 *    EntityManager entityManager;
 *    
 *    {@code @}Test
 *    public void myTestMethod()
 *    {
 *       entityManager.persist(new Foo());
 *    }
 *    
 * }
 * </pre>
 * 
 * You can use a different {@link PersistenceUnitFactory} implementation class name in your
 * {@code @}PersistenceUnitBuiltBy annotation if your test needs a different kind of persistence
 * unit.
 * 
 * By default, a transaction will be started before your test method executes and will be rolled back after
 * it executes.  You can cancel this transaction management by annotation your EntityManager field with
 * {@code @ManualTransactionControl}.
 * 
 * 
 * @author Matt Drees
 */
public class PersistenceContextInjectionListener implements ITestNGListener, IInvokedMethodListener, ISuiteListener
{

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult)
    {
        if (method.getTestMethod().isBeforeMethodConfiguration() ||
                method.isTestMethod())
        {
            setupEntityManagers(method.getTestMethod(), testResult);
        }
    }

    private void setupEntityManagers(ITestNGMethod testNGMethod, ITestResult testResult)
    {
        Set<PersistenceContextReference> persistenceContextReferences = findPersistenceContextsReferencedByClass(testNGMethod);
        for (PersistenceContextReference persistenceContextReference : persistenceContextReferences)
        {
            if (persistenceContextReference.getEntityManager() == null)
            {
                EntityManager entityManager = createEntityManager(persistenceContextReference, testResult);
                persistenceContextReference.inject(entityManager);
                if (persistenceContextReference.automaticTransactionControl())
                {
                    entityManager.getTransaction().begin();
                }
            }
        }
    }

    private EntityManager createEntityManager(PersistenceContextReference persistenceContextReference, ITestResult testResult)
    {
        EntityManagerFactory factory = getEntityManagerFactory(persistenceContextReference);
        EntityManager entityManager = factory.createEntityManager();
        return entityManager;
    }


    
    private final Map<String, EntityManagerFactory> persistenceUnits = Maps.newHashMap();
    
    private synchronized EntityManagerFactory getEntityManagerFactory(PersistenceContextReference persistenceContextReference)
    {
        String unitName = persistenceContextReference.getUnitName();
        if (persistenceUnits.containsKey(unitName))
        {
            return persistenceUnits.get(unitName);
        }
        else
        {
            EntityManagerFactory emf = createEntityManagerFactory(persistenceContextReference);
            persistenceUnits.put(unitName, emf);
            return emf;
        }
    }

    private EntityManagerFactory createEntityManagerFactory(PersistenceContextReference persistenceContextReference)
    {
        Class<? extends PersistenceUnitFactory> persistenceUnitFactoryClass = persistenceContextReference.getPersistenceUnitFactoryClass();
        try
        {
            PersistenceUnitFactory persistenceUnitFactory = persistenceUnitFactoryClass.newInstance();
            return persistenceUnitFactory.buildEntityManagerFactory(persistenceContextReference.getUnitName());
        }
        catch (InstantiationException e)
        {
            throw Throwables.propagate(e);
        }
        catch (IllegalAccessException e)
        {
            throw Throwables.propagate(e);
        }
    }

    
    

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult)
    {
        if (method.isTestMethod())
        {
            rollbackAndCloseEntityManagers(method.getTestMethod());
        }
    }


    private void rollbackAndCloseEntityManagers(ITestNGMethod testNGMethod)
    {
        Set<PersistenceContextReference> persistenceContextReferences = findPersistenceContextsReferencedByClass(testNGMethod);
        for (PersistenceContextReference persistenceContextReference : persistenceContextReferences)
        {
            EntityManager entityManager = persistenceContextReference.getEntityManager();
            if (entityManager == null)
            {
                throw new RuntimeException(String.format("It appears the '%s' EntityManager reference in %s was cleared; please don't do this!",
                    persistenceContextReference.getUnitName(),
                    persistenceContextReference.getField()
                    ));
            }

            if (persistenceContextReference.automaticTransactionControl())
            {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
            persistenceContextReference.clear();
        }
    }

    private Set<PersistenceContextReference> findPersistenceContextsReferencedByClass(ITestNGMethod testNGMethod)
    {
        Set<PersistenceContextReference> references = Sets.newHashSet();
        for (Class<?> clazz : Iterables.reverse(Classes.classHierarchyOf(testNGMethod.getRealClass())))
        {
            if (clazz.isAnnotationPresent(PersistenceUnitBuiltBy.class))
            {
                collectPersistenceContextReferences(testNGMethod, references, clazz);
            }
        }
        return references;
    }

    private void collectPersistenceContextReferences(ITestNGMethod testNGMethod,
                                                     Set<PersistenceContextReference> references, Class<?> clazz)
    {
        for (Field field : Annotations.getFieldsAnnotated(clazz, PersistenceContext.class))
        {
            if (!EntityManager.class.isAssignableFrom(field.getType()))
            {
                throw new RuntimeException("@PersistenceContext can only go on EntityManager fields; this field is wrong: " + field);
            }
            references.add(new PersistenceContextReference(field, testNGMethod.getInstance()));
        }
    }

    @Override
    public void onStart(ISuite suite)
    {
    }

    @Override
    public void onFinish(ISuite suite)
    {
        closeEntityManagerFactories();
    }


    private void closeEntityManagerFactories()
    {
        for (EntityManagerFactory factory : persistenceUnits.values())
        {
            factory.close();   
        }
        persistenceUnits.clear();
    }



}
