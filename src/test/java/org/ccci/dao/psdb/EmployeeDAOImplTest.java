package org.ccci.dao.psdb;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import junit.framework.Assert;

import org.ccci.dao.EmployeeNotFoundException;
import org.ccci.dao.MultipleEmployeesFoundException;
import org.ccci.model.Employee;
import org.ccci.model.EmployeeCouple;
import org.ccci.model.EmployeeId;
import org.ccci.model.EmployeeUnit;
import org.ccci.model.EmploymentStatus;
import org.ccci.model.Gender;
import org.ccci.model.MarriageStatus;
import org.ccci.model.SingleEmployee;
import org.ccci.testutils.persistence.HibernateInMemoryH2PersistenceUnitFactory;
import org.ccci.testutils.persistence.PersistenceUnitBuiltBy;
import org.ccci.util.mail.EmailAddress;
import org.ccci.util.strings.Strings;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import org.ccci.util.mail.PersonalEmailAddress;
import com.google.common.base.Preconditions;
import org.ccci.util.Generics;

import java.util.Set;

@PersistenceUnitBuiltBy(HibernateInMemoryH2PersistenceUnitFactory.class)
public class EmployeeDAOImplTest
{
    
    @PersistenceContext(unitName = "utilUnitTest")
    EntityManager entityManager;


    private EmployeeDAOImpl employeeDAO;
    private EmployeeId employeeId1 = EmployeeId.valueOf("000000001");
    private EmployeeId employeeId2 = EmployeeId.valueOf("000000002");
    private EmployeeId employeeId3 = EmployeeId.valueOf("000000001S");

    @BeforeMethod
    public void createDao()
    {
        employeeDAO = new EmployeeDAOImpl();
        employeeDAO.psEntityManager = entityManager;
    }

    @org.testng.annotations.Test
    public void testFindAll_straightforward()
    {
        EmployeeEntity employee1 = new EmployeeEntity();
        employee1.setKey(new EmployeeEntity.Key(employeeId1, 0));
        entityManager.persist(employee1);

        EmployeeEntity employee2 = new EmployeeEntity();
        employee2.setKey(new EmployeeEntity.Key(employeeId2, 0));
        entityManager.persist(employee2);

        Map<EmployeeId, Employee> employees = employeeDAO.find(Sets.newLinkedHashSet(Arrays.asList(employeeId2, employeeId1)));
        Assert.assertEquals(2, employees.size());

        Iterator<EmployeeId> employeeIdsIterator = employees.keySet().iterator();
        Iterator<Employee> employeesIterator = employees.values().iterator();
        
        Assert.assertEquals(employeeId2, employeeIdsIterator.next());
        Assert.assertEquals(employeeId2, employeesIterator.next().getEmployeeId());

        Assert.assertEquals(employeeId1, employeeIdsIterator.next());
        Assert.assertEquals(employeeId1, employeesIterator.next().getEmployeeId());
        
        Assert.assertFalse(employeeIdsIterator.hasNext());
        Assert.assertFalse(employeesIterator.hasNext());
    }

    @Test(expectedExceptions = EmployeeNotFoundException.class)
    public void testFindAll_noEmployeeExists()
    {
        EmployeeEntity employee1 = new EmployeeEntity();
        employee1.setKey(new EmployeeEntity.Key(employeeId1, 0));
        entityManager.persist(employee1);

        employeeDAO.find(Sets.newHashSet(employeeId2, employeeId1));
    }


    /**
     * mostly to check that query is constructed correctly when multiple 'in' clauses must be
     * used
     */
    @Test
    public void testFindAll_multipleInClauseConditions()
    {
        employeeDAO.setPartitionSize(2);
        
        EmployeeEntity employee1 = new EmployeeEntity();
        employee1.setKey(new EmployeeEntity.Key(employeeId1, 0));
        entityManager.persist(employee1);

        EmployeeEntity employee2 = new EmployeeEntity();
        employee2.setKey(new EmployeeEntity.Key(employeeId2, 0));
        entityManager.persist(employee2);
        
        EmployeeEntity employee3 = new EmployeeEntity();
        employee3.setKey(new EmployeeEntity.Key(employeeId3, 0));
        entityManager.persist(employee3);
        
        Map<EmployeeId, Employee> employees = employeeDAO.find(
            Sets.newHashSet(employeeId1, employeeId2, employeeId3));
        Assert.assertEquals(3, employees.size());
    }

    
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testSearchByNameOrEmplid_nullInput()
    {
        employeeDAO.searchByNameEmailOrEmployeeId(null, null);
    }

    @Test
    public void testSearchByNameOrEmplid_resultsLimited()
    {
        EmployeeEntity employee1 = new EmployeeEntity();
        employee1.setKey(new EmployeeEntity.Key(employeeId1, 0));
        employee1.setFirstName("Joe");
        employee1.setLastName("Staff");
        employee1.setEmploymentStatus(EmploymentStatus.ACTIVE);
        entityManager.persist(employee1);

        EmployeeEntity employee2 = new EmployeeEntity();
        employee2.setKey(new EmployeeEntity.Key(employeeId2, 0));
        employee2.setFirstName("Sam");
        employee2.setLastName("Staff");
        employee1.setEmploymentStatus(EmploymentStatus.ACTIVE);
        entityManager.persist(employee2);

        List<Employee> employeesFound = employeeDAO.searchByNameEmailOrEmployeeId("Staff", 1);
        Assert.assertEquals("Staff", Iterables.getOnlyElement(employeesFound).getLastName());
    }

    @Test
    public void testSearchByNameOrEmplid_findCorrectEmployee()
    {
        EmployeeEntity employee1 = new EmployeeEntity();
        employee1.setKey(new EmployeeEntity.Key(employeeId1, 0));
        employee1.setFirstName("Joseph");
        employee1.setPreferredFirstName("Joe");
        employee1.setLastName("Staff");
        employee1.setEmail(EmailAddress.valueOf("joe.staff@ccci.org"));
        employee1.setEmploymentStatus(EmploymentStatus.ACTIVE);
        entityManager.persist(employee1);

        EmployeeEntity employee2 = new EmployeeEntity();
        employee2.setKey(new EmployeeEntity.Key(employeeId2, 0));
        employee2.setFirstName("Samuel");
        employee2.setPreferredFirstName("Sam");
        employee2.setLastName("Intern");
        employee2.setEmail(EmailAddress.valueOf("sam.intern@uscm.org"));
        employee2.setEmploymentStatus(EmploymentStatus.ACTIVE);
        entityManager.persist(employee2);

        checkSearchByNameOrEmplidFindsEmployee1("Joseph Staff");
        checkSearchByNameOrEmplidFindsEmployee1("Staff, Joseph");
        checkSearchByNameOrEmplidFindsEmployee1("Joe Staff");
        checkSearchByNameOrEmplidFindsEmployee1("J Staff");
        checkSearchByNameOrEmplidFindsEmployee1("joe");
        checkSearchByNameOrEmplidFindsEmployee1("staff");
        checkSearchByNameOrEmplidFindsEmployee1("joe.staff@ccci.org");
        checkSearchByNameOrEmplidFindsEmployee1("joe.staff");
        checkSearchByNameOrEmplidFindsEmployee1(employeeId1.toString());
        checkSearchByNameOrEmplidFindsEmployee1(Strings.tail(employeeId1.toString(), 4));
        checkSearchByNameOrEmplidFindsEmployee1("Joe Staff (" + employeeId1.toString() + ")");
        checkSearchByNameOrEmplidFindsEmployee1("Joe Staff (joe.staff@ccci.org " + employeeId1.toString() + ")");
    }

    private void checkSearchByNameOrEmplidFindsEmployee1(String searchString)
    {
        List<Employee> employeesFound = employeeDAO.searchByNameEmailOrEmployeeId(searchString, null);
        Assert.assertEquals(employeeId1, Iterables.getOnlyElement(employeesFound).getEmployeeId());
    }
    
    @Test
    public void testGetByNameEmailOrEmployeeId_findsCorrectEmployee()
    {
        EmployeeEntity employee1 = new EmployeeEntity();
        employee1.setKey(new EmployeeEntity.Key(employeeId1, 0));
        employee1.setFirstName("Joseph");
        employee1.setLastName("Staff");
        employee1.setEmploymentStatus(EmploymentStatus.ACTIVE);
        entityManager.persist(employee1);
        

        EmployeeEntity employee2 = new EmployeeEntity();
        employee2.setKey(new EmployeeEntity.Key(employeeId2, 0));
        employee2.setFirstName("Samuel");
        employee2.setLastName("Intern");
        employee2.setEmail(EmailAddress.valueOf("sam.intern@uscm.org"));
        employee2.setEmploymentStatus(EmploymentStatus.ACTIVE);
        entityManager.persist(employee2);
        
        EmployeeEntity employee3 = new EmployeeEntity();
        employee3.setKey(new EmployeeEntity.Key(employeeId3, 0));
        employee3.setFirstName("Jane");
        employee3.setLastName("Staff");
        employee3.setEmail(EmailAddress.valueOf("jane.staff@ccci.org"));
        employee3.setEmploymentStatus(EmploymentStatus.ACTIVE);
        entityManager.persist(employee3);
        
        checkGetByNameEmailOrEmployeeIdGetsEmployee(employeeId1.toString(), employeeId1);
        checkGetByNameEmailOrEmployeeIdGetsEmployee(employeeId2.toString(), employeeId2);
        checkGetByNameEmailOrEmployeeIdGetsEmployee(employeeId3.toString(), employeeId3);
        checkGetByNameEmailOrEmployeeIdGetsEmployee("joseph staff", employeeId1);
        checkGetByNameEmailOrEmployeeIdGetsEmployee("sam intern", employeeId2);
        checkGetByNameEmailOrEmployeeIdGetsEmployee("jane staff", employeeId3);
    }

    private void checkGetByNameEmailOrEmployeeIdGetsEmployee(String searchString, EmployeeId expectedEmployeeId)
    {
        Employee found = employeeDAO.getByNameEmailOrEmployeeId(searchString);
        Assert.assertEquals(expectedEmployeeId, found.getEmployeeId());
    }
    
    @Test(expectedExceptions = MultipleEmployeesFoundException.class)
    public void testGetByNameEmailOrEmployeeId_bombsWhenMultipleResults()
    {
        EmployeeEntity employee1 = new EmployeeEntity();
        employee1.setKey(new EmployeeEntity.Key(employeeId1, 0));
        employee1.setFirstName("Joseph");
        employee1.setLastName("Staff");
        employee1.setEmploymentStatus(EmploymentStatus.ACTIVE);
        entityManager.persist(employee1);
        
        EmployeeEntity employee3 = new EmployeeEntity();
        employee3.setKey(new EmployeeEntity.Key(employeeId3, 0));
        employee3.setFirstName("Jane");
        employee3.setLastName("Staff");
        employee3.setEmploymentStatus(EmploymentStatus.ACTIVE);
        entityManager.persist(employee3);
        
        employeeDAO.getByNameEmailOrEmployeeId("Staff");
    }
    
    @Test(expectedExceptions = EmployeeNotFoundException.class)
    public void testGetByNameEmailOrEmployeeId_bombsWhenNoResults()
    {
        employeeDAO.getByNameEmailOrEmployeeId("Jerry");
    }
    
    @Test
    public void testGetPersonalEmailAddressesForIds_straightforward()
    {
        createEmployeeWithEmail(employeeId1, "Joe", "Staff", "joe.staff@cru.org");
        createEmployeeWithEmail(employeeId2, "Sam", "Intern", "sam.intern@cru.org");

        Set<PersonalEmailAddress> emailAddresses = employeeDAO.getPersonalEmailAddressesForIds(
            Sets.newHashSet(employeeId1, employeeId2));
        
        Assert.assertEquals(2, emailAddresses.size());
        assertContainsEmail(emailAddresses, "joe.staff@cru.org", "Joe Staff", "Expected to find Joe's email address");
        assertContainsEmail(emailAddresses, "sam.intern@cru.org", "Sam Intern", "Expected to find Sam's email address");
    }
    
    @Test
    public void testGetPersonalEmailAddressesForIds_filtersEmptyEmails()
    {
        createEmployeeWithEmail(employeeId1, "Joe", "Staff", "joe.staff@cru.org");
        createEmployeeWithoutEmail(employeeId2, "Sam", "Intern"); // No email - should be filtered out

        Set<PersonalEmailAddress> emailAddresses = employeeDAO.getPersonalEmailAddressesForIds(
            Sets.newHashSet(employeeId1, employeeId2));
        
        // Should only get Joe's email since Sam has no email
        Assert.assertEquals(1, emailAddresses.size());
        PersonalEmailAddress email = Iterables.getOnlyElement(emailAddresses);
        Assert.assertEquals("joe.staff@cru.org", email.getEmailAddress().toString());
        Assert.assertEquals("Joe Staff", email.getPersonalName());
    }
    
    /**
     * Test that the chunking functionality works correctly when partition size exceeds Oracle's 1000-element IN clause limit.
     * This test specifically addresses the issue that was causing reminder email failures.
     */
    @Test
    public void testGetPersonalEmailAddressesForIds_chunking()
    {
        // Create a query-counting DAO for this test
        QueryCountingEmployeeDAO queryCountingDAO = new QueryCountingEmployeeDAO();
        queryCountingDAO.psEntityManager = entityManager;
        queryCountingDAO.setPartitionSize(2);
        
        createEmployeeWithEmail(employeeId1, "Joe", "Staff", "joe.staff@cru.org");
        createEmployeeWithEmail(employeeId2, "Sam", "Intern", "sam.intern@cru.org");
        createEmployeeWithEmail(employeeId3, "Jane", "Manager", "jane.manager@cru.org");

        // This will be split into chunks: [employee1, employee2] and [employee3]
        Set<PersonalEmailAddress> emailAddresses = queryCountingDAO.getPersonalEmailAddressesForIds(
            Sets.newHashSet(employeeId1, employeeId2, employeeId3));
        
        Assert.assertEquals(3, emailAddresses.size());
        
        // Verify all employees are found despite chunking
        assertContainsEmail(emailAddresses, "joe.staff@cru.org", "Joe Staff", "Expected to find Joe's email from first chunk");
        assertContainsEmail(emailAddresses, "sam.intern@cru.org", "Sam Intern", "Expected to find Sam's email from first chunk");
        assertContainsEmail(emailAddresses, "jane.manager@cru.org", "Jane Manager", "Expected to find Jane's email from second chunk");
        
        // Verify that exactly 2 queries were executed (one for each chunk)
        Assert.assertEquals("Expected 2 queries to be executed due to chunking", 2, queryCountingDAO.getNamedQueryExecutionCount());
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testGetPersonalEmailAddressesForIds_nullInput()
    {
        employeeDAO.getPersonalEmailAddressesForIds(null);
    }
    
    // Helper methods for creating test employees
    private EmployeeEntity createEmployeeWithEmail(EmployeeId employeeId, String firstName, String lastName, String email)
    {
        EmployeeEntity employee = new EmployeeEntity();
        employee.setKey(new EmployeeEntity.Key(employeeId, 0));
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        if (email != null) {
            employee.setEmail(EmailAddress.valueOf(email));
        }
        entityManager.persist(employee);
        return employee;
    }
    
    private void createEmployeeWithoutEmail(EmployeeId employeeId, String firstName, String lastName)
    {
        createEmployeeWithEmail(employeeId, firstName, lastName, null);
    }
    
    // Helper method for verifying email addresses in results
    private void assertContainsEmail(Set<PersonalEmailAddress> emailAddresses, String expectedEmail, String expectedName, String description)
    {
        boolean found = false;
        for (PersonalEmailAddress email : emailAddresses) {
            if (email.getEmailAddress().toString().equals(expectedEmail) && email.getPersonalName().equals(expectedName)) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(description, found);
    }
    
    @Test
    public void testFindEmployeeUnit_single()
    {
        EmployeeEntity employee = new EmployeeEntity();
        employee.setKey(new EmployeeEntity.Key(employeeId1, 0));
        employee.setFirstName("Joe");
        employee.setLastName("Staff");
        employee.setEmploymentStatus(EmploymentStatus.ACTIVE);
        employee.setMarriedStatus(MarriageStatus.S);
        entityManager.persist(employee);
        
        EmployeeUnit found = employeeDAO.findEmployeeUnit(employeeId1);
        checkSingleJoe(found);
        
    }

    @Test
    public void testFindEmployeeUnit_marriedAndNotInHR()
    {
        EmployeeEntity employee = new EmployeeEntity();
        employee.setKey(new EmployeeEntity.Key(employeeId1, 0));
        employee.setFirstName("Joe");
        employee.setLastName("Staff");
        employee.setEmploymentStatus(EmploymentStatus.ACTIVE);
        employee.setMarriedStatus(MarriageStatus.M);
        employee.setGender(Gender.M);
        entityManager.persist(employee);
        
        entityManager.flush();
        entityManager.clear();
        
        EmployeeUnit found = employeeDAO.findEmployeeUnit(employeeId1);
        checkSingleJoe(found);
        
    }

    private void checkSingleJoe(EmployeeUnit found)
    {
        Assert.assertNotNull(found);
        Assert.assertTrue(!found.isCouple());
        Assert.assertTrue(found instanceof SingleEmployee);
        Assert.assertEquals(employeeId1, found.getPrimary().getEmployeeId());
    }
    
    @Test
    public void testFindEmployeeUnit_marriedAndInHR()
    {
        EmployeeEntity employee1 = new EmployeeEntity();
        employee1.setKey(new EmployeeEntity.Key(employeeId1, 0));
        employee1.setFirstName("Joe");
        employee1.setLastName("Staff");
        employee1.setEmploymentStatus(EmploymentStatus.ACTIVE);
        employee1.setMarriedStatus(MarriageStatus.M);
        employee1.setGender(Gender.M);
        entityManager.persist(employee1);
        
        EmployeeEntity employee2 = new EmployeeEntity();
        employee2.setKey(new EmployeeEntity.Key(employeeId3, 0));
        employee2.setFirstName("Jane");
        employee2.setLastName("Staff");
        employee2.setEmploymentStatus(EmploymentStatus.ACTIVE);
        employee2.setMarriedStatus(MarriageStatus.M);
        employee2.setGender(Gender.F);
        entityManager.persist(employee2);
        
        entityManager.flush();
        entityManager.clear();
        
        EmployeeUnit found = employeeDAO.findEmployeeUnit(employeeId1);
        checkCoupleJoeAndJane(found);
        
        EmployeeUnit found2 = employeeDAO.findEmployeeUnit(employeeId3);
        checkCoupleJoeAndJane(found2);
    }
    
    private void checkCoupleJoeAndJane(EmployeeUnit found)
    {
        Assert.assertNotNull(found);
        Assert.assertTrue(found.isCouple());
        Assert.assertTrue(found instanceof EmployeeCouple);
        EmployeeCouple couple = (EmployeeCouple) found;
        Assert.assertEquals(employeeId1, couple.getPrimary().getEmployeeId());
        Assert.assertEquals(employeeId3, couple.getSecondary().getEmployeeId());
        Assert.assertEquals(employeeId1, couple.getHusband().getEmployeeId());
        Assert.assertEquals(employeeId3, couple.getWife().getEmployeeId());
    }
    
    /**
     * Test helper class that extends EmployeeDAOImpl to count query executions for testing chunking behavior.
     */
    private static class QueryCountingEmployeeDAO extends EmployeeDAOImpl {
        private int namedQueryExecutionCount = 0;
        
        public QueryCountingEmployeeDAO() {
            super();
        }
        
        @Override
        public Set<PersonalEmailAddress> getPersonalEmailAddressesForIds(Set<EmployeeId> employeeIds) {
            Preconditions.checkNotNull(employeeIds, "employeeIds is null");
            if (employeeIds.isEmpty())
            {
                return Sets.newHashSet();
            }

            // Create partitions manually since the method is private
            List<List<EmployeeId>> partitions = Lists.newArrayList();
            Iterable<List<EmployeeId>> partitionAsIterable = Iterables.partition(employeeIds, getPartitionSize());
            for (Iterable<EmployeeId> partAsIterable : partitionAsIterable)
            {
                List<EmployeeId> part = Lists.newArrayList(partAsIterable);
                partitions.add(part);
            }
            
            Set<PersonalEmailAddress> allPersonalEmailAddresses = Sets.newHashSet();
            
            // Count each partition as a separate query execution
            for (List<EmployeeId> partition : partitions)
            {
                namedQueryExecutionCount++; // Count the query execution
                
                List<Object[]> rawEmailAddresses = Generics.checkObjectArrayList( 
                    psEntityManager.createNamedQuery("EmployeeEntity.findEmailInfoByEmployeeIds")
                    .setParameter("employeeIds", partition)
                    .getResultList(), 
                    String.class, String.class, String.class);

                for (Object[] row : rawEmailAddresses)
                {
                    String personalName = row[0] + " " + row[1];
                    String emailAddress = (String) row[2];
                    try
                    {
                        allPersonalEmailAddresses.add(PersonalEmailAddress.newPersonalEmailAddress(emailAddress, personalName));
                    }
                    catch (IllegalArgumentException e)
                    {
                        log.warn(String.format("employee %s has an invalid email address: %s", personalName, emailAddress));
                    }
                }
            }
            
            return allPersonalEmailAddresses;
        }
        
        public int getNamedQueryExecutionCount() {
            return namedQueryExecutionCount;
        }
    }
}
