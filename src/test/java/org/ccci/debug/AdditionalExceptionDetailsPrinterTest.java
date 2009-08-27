package org.ccci.debug;

import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AdditionalExceptionDetailsPrinterTest
{

    Logger log = Logger.getLogger(AdditionalExceptionDetailsPrinterTest.class);
    
    AdditionalExceptionDetailsPrinter printer;
    
    @BeforeClass
    public static void setupLogging()
    {
        BasicConfigurator.configure();
    }
    
    @BeforeMethod
    public void setupPrinter()
    {
        printer = new AdditionalExceptionDetailsPrinter();
    }
    
    @Test
    public void testPrintConstraintViolationException()
    {
        SQLException sqle = new BatchUpdateException("ORA-blahblahblah: unique constraint violated", "23505", 234, new int[]{1, 1, Statement.SUCCESS_NO_INFO, Statement.EXECUTE_FAILED});
        ConstraintViolationException cve = new ConstraintViolationException("Could not execute JDBC batch update", sqle , "insert into foo(id) values (42)", "foo_id_constraint");
        List<String> additionalDetails = printer.getAdditionalDetails(cve);
        Assert.assertTrue(additionalDetails.size() > 1);
        for (String detail : additionalDetails)
        {
            log.debug(detail);
        }
    }
    
}
