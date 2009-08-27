package org.ccci.testutils;


import org.ccci.testutils.persistence.HibernatePersistenceTest;

public class PersistentUtilTest extends HibernatePersistenceTest
{

    @Override
    protected String getDefaultPersistenceUnitName()
    {
        return "utilUnitTest";
    }

}
