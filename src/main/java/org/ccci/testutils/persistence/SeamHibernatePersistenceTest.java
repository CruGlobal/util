package org.ccci.testutils.persistence;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.ejb.HibernatePersistence;

import com.google.common.base.Preconditions;

public abstract class SeamHibernatePersistenceTest extends SeamPersistenceTest
{
	   @Override
	    public EntityManagerFactory buildEntityManagerFactory()
	    {
	        return buildFailoverEntityManagerFactory();
	    }
	    
	    public EntityManagerFactory buildFailoverEntityManagerFactory()
	    {
	        Properties properties = new Properties();

	        // Ensure RESOURCE_LOCAL transactions is used.
	        properties.put("javax.persistence.transactionType",
	            PersistenceUnitTransactionType.RESOURCE_LOCAL.name());
	        
	        //note: this is not always sufficient; sometimes eclipse-link will still take the persistence unit during the
	        //persistence provider scan
	        properties.put("javax.persistence.provider", org.hibernate.ejb.HibernatePersistence.class.getName());
	        
	        properties.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
	        properties.put("hibernate.connection.provider_class", "org.ccci.ccp.failover.FailoverDatasourceConnectionProvider");
	        properties.put("hibernate.connection.driver_class", "oracle.jdbc.xa.client.OracleXADataSource");
	        properties.put("hibernate.connection.username", "dsstest");
	        properties.put("hibernate.connection.password", "dsstest");
	        properties.put("hibernate.connection.url",  "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL = TCP)(HOST = harta9210v)(PORT = 1521)))(CONNECT_DATA =(SERVER = DEDICATED)(SERVICE_NAME = crmdapp.ccci.org)(FAILOVER_MODE=(TYPE = SELECT)(METHOD = BASIC)(RETRIES = 20)(DELAY= 1))))");
//	        properties.put("hibernate.hbm2ddl.auto", "create-drop");
	        properties.put("hibernate.show_sql", isShowSql());
	        properties.put("hibernate.format_sql", "true");
	        properties.put("hibernate.jdbc.batch_size", "0");
	        properties.put("hibernate.query.jpaql_strict_compliance", "true");
	        properties.put("hibernate.validator.autoregister_listeners", false);
	   
	        //failover custom properties
	        properties.put("org.ccci.failover.dialect", "org.hibernate.dialect.Oracle10gDialect");
	        properties.put("org.ccci.failover.url", "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS_LIST =(LOAD_BALANCE = YES)(FAILOVER = ON)(ADDRESS = (PROTOCOL = TCP)(HOST = harta9210v)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = harta9310v)(PORT = 1521)))(CONNECT_DATA =(SERVER = DEDICATED)(SERVICE_NAME = psdev.ccci.org)(FAILOVER_MODE =(TYPE = SELECT)(METHOD = BASIC)(RETRIES = 20)(DELAY= 1))))");
	        properties.put("org.ccci.failover.driver_class", "oracle.jdbc.xa.client.OracleXADataSource");
	        properties.put("org.ccci.failover.pool_size", "10");
	        properties.put("org.ccci.failover.username", "nkopp");
	        properties.put("org.ccci.failover.password", "nkopp");
//	        properties.put("org.ccci.failover.autocommit", new Boolean(false));
	        
	        return Preconditions.checkNotNull(new HibernatePersistence().createEntityManagerFactory(getPersistenceUnitName(), properties), "unable to create hibernate persistence unit");
	    }

	   
	    //a subclass could print sql if it wants to.
	    //maybe someday make this annotation-driven.
	    protected String isShowSql()
	    {
	        return "false";
	    }
}
