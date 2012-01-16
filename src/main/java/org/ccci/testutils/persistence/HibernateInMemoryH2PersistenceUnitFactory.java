package org.ccci.testutils.persistence;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.ccci.testutils.persistence.PersistenceUnitFactory;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.ejb.HibernatePersistence;
import org.hibernate.service.jdbc.connections.internal.DriverManagerConnectionProviderImpl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class HibernateInMemoryH2PersistenceUnitFactory implements PersistenceUnitFactory
{

    @Override
    public EntityManagerFactory buildEntityManagerFactory(String unitName)
    {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(unitName),
            "@PersistenceContext annotation must define a non-null unitName");
        
        Properties properties = new Properties();

        properties.put("javax.persistence.transactionType",
            PersistenceUnitTransactionType.RESOURCE_LOCAL.name());
        
        properties.put("javax.persistence.provider", org.hibernate.ejb.HibernatePersistence.class.getName());
        
        properties.put("hibernate.dialect", H2Dialect.class.getName());
        properties.put("hibernate.connection.provider_class", DriverManagerConnectionProviderImpl.class.getName());
        properties.put("hibernate.connection.driver_class", org.h2.Driver.class.getName() );
        properties.put("hibernate.connection.username", "sa");
        properties.put("hibernate.connection.password", "");
        properties.put("hibernate.connection.url", "jdbc:h2:mem:hibernatePersistenceTest;DB_CLOSE_DELAY=-1;MVCC=TRUE");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.jdbc.batch_size", "0");
        
        //disable validation by default (not generally what we want in unit tests, so we don't have to set up entire graphs):
        properties.put("hibernate.validator.autoregister_listeners", false);
        properties.put("javax.persistence.validation.mode", "none");
        
        return Preconditions.checkNotNull(
            new HibernatePersistence()
                .createEntityManagerFactory(unitName, properties), 
            "unable to create hibernate persistence unit '" + unitName + 
            "'- this generally means that a persistence.xml file that defines this persistence unit is not on the classpath." +
            " You may need to manually run a build command to create or refresh the test resources.");
    }

}
