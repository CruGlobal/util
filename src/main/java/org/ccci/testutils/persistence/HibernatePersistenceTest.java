package org.ccci.testutils.persistence;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.ejb.HibernatePersistence;

import com.google.common.base.Preconditions;


/**
 * A simple helper for tests which need a live entityManager.
 * A new entityManager and transaction is created for each test;
 * after the test the transaction is rolled back.
 * 
 * Uses Hibernate's JPA implementation and an in-memory HSQL
 * database.
 * 
 * Subclasses may use jUnit or testNG
 * 
 * Picks up persistent classes in eTimesheet-ejb/bin.
 * The path is hardcoded.  
 * See eTimesheet-test/ejbTests/META-INF/persistence.xml for comments
 * on configuration.
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
        
        //note: this is not always sufficient; sometimes eclipse-link will still take the persistence unit during the
        //persistence provider scan
        properties.put("javax.persistence.provider", org.hibernate.ejb.HibernatePersistence.class.getName());
        
        properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        properties.put("hibernate.connection.provider_class", "org.hibernate.connection.DriverManagerConnectionProvider");
        properties.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
        properties.put("hibernate.connection.username", "sa");
        properties.put("hibernate.connection.password", "");
        properties.put("hibernate.connection.url", "jdbc:hsqldb:.");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.show_sql", isShowSql());
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.jdbc.batch_size", "0");
        properties.put("hibernate.query.jpaql_strict_compliance", "true");
        properties.put("hibernate.validator.autoregister_listeners", false);
        
        return Preconditions.checkNotNull(new HibernatePersistence().createEntityManagerFactory(getPersistenceUnitName(), properties), "unable to create hibernate persistence unit");
    }

   
    //a subclass could print sql if it wants to.
    //maybe someday make this annotation-driven.
    protected String isShowSql()
    {
        return "false";
    }

}
