package org.ccci.testutils.persistence;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.ejb.HibernatePersistence;

import com.google.common.base.Preconditions;


/**
 * A simple helper for tests which need a live {@link EntityManager}.
 * A new {@link EntityManager} and transaction is created for each test;
 * after the test the transaction is rolled back.
 * 
 * Uses Hibernate's JPA implementation and an in-memory H2
 * database.
 * 
 * Subclasses may use jUnit or testNG
 * 
 * @author Matt Drees
 *
 */
public abstract class HibernatePersistenceTest extends PersistenceTest
{

    @Override
    public EntityManagerFactory buildEntityManagerFactory()
    {
        
        Properties properties = new Properties();

        // Ensure RESOURCE_LOCAL transactions is used.
        properties.put("javax.persistence.transactionType",
            PersistenceUnitTransactionType.RESOURCE_LOCAL.name());
        
        properties.put("javax.persistence.provider", org.hibernate.ejb.HibernatePersistence.class.getName());
        
        properties.put("hibernate.dialect", H2Dialect.class.getName());
        properties.put("hibernate.connection.provider_class", org.hibernate.connection.DriverManagerConnectionProvider.class.getName());
        properties.put("hibernate.connection.driver_class", org.h2.Driver.class.getName() );
        properties.put("hibernate.connection.username", "sa");
        properties.put("hibernate.connection.password", "");
        properties.put("hibernate.connection.url", "jdbc:h2:mem:hibernatePersistenceTest;DB_CLOSE_DELAY=-1;MVCC=TRUE");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.show_sql", isShowSql());
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.jdbc.batch_size", "0");
        properties.put("hibernate.query.jpaql_strict_compliance", "true");
        
        //disable validation by default (not generally what we want in unit tests, so we don't have to set up entire graphs):
        properties.put("hibernate.validator.autoregister_listeners", false);
        properties.put("javax.persistence.validation.mode", "none");
        
        return Preconditions.checkNotNull(
            new HibernatePersistence()
                .createEntityManagerFactory(getPersistenceUnitName(), properties), 
            "unable to create hibernate persistence unit '" + getPersistenceUnitName() + 
            "'- this generally means that a persistence.xml file that defines this persistence unit is not on the classpath." +
            " You may need to manually run a build command to create or refresh the test resources.");
    }

   
    //a subclass could print sql if it wants to.
    //maybe someday make this annotation-driven.
    protected String isShowSql()
    {
        return "false";
    }

}
