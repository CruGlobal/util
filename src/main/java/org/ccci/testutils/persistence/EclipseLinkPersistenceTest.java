package org.ccci.testutils.persistence;

import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DROP_AND_CREATE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_DRIVER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_PASSWORD;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MIN;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_URL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_USER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MIN;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LEVEL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_SESSION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_THREAD;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_TIMESTAMP;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TARGET_DATABASE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TARGET_SERVER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TRANSACTION_TYPE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.WEAVING;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.ccci.eclipselink.platform.database.HSQL18SequencePlatform;
import org.eclipse.persistence.config.TargetServer;

/** 
 * A simple helper for tests which need a live entityManager.
 * A new entityManager and transaction is created for each test;
 * after the test the transaction is rolled back.
 * 
 * Uses EclipseLink's JPA implementation and an in-memory HSQL
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
public abstract class EclipseLinkPersistenceTest extends PersistenceTest
{

    @Override
    public EntityManagerFactory buildEntityManagerFactory()
    {
        Properties properties = new Properties();
        properties.put("javax.persistence.provider",
            "org.eclipse.persistence.jpa.PersistenceProvider");

        // Ensure RESOURCE_LOCAL transactions is used.
        properties.put(TRANSACTION_TYPE,
            PersistenceUnitTransactionType.RESOURCE_LOCAL.name());
     
        // Configure the internal EclipseLink connection pool
        properties.put(JDBC_DRIVER, "org.hsqldb.jdbcDriver");
        properties.put(JDBC_URL, "jdbc:hsqldb:.");
        properties.put(JDBC_USER, "sa");
        properties.put(JDBC_PASSWORD, "");
        properties.put(JDBC_READ_CONNECTIONS_MIN, "1");
        properties.put(JDBC_WRITE_CONNECTIONS_MIN, "1");
     
        // Configure logging. FINE ensures all SQL is shown
        properties.put(LOGGING_LEVEL, "FINE");
        properties.put(LOGGING_TIMESTAMP, "false");
        properties.put(LOGGING_THREAD, "false");
        properties.put(LOGGING_SESSION, "false");

        properties.put(TARGET_SERVER, TargetServer.None);
        properties.put(TARGET_DATABASE, HSQL18SequencePlatform.class.getName());
        properties.put(WEAVING, "false");
        
        properties.put(DDL_GENERATION, DROP_AND_CREATE);

        return Persistence.createEntityManagerFactory(getPersistenceUnitName(), properties); 
    }

}
