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
import org.ccci.util.mail.PersonalEmailAddress;

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
        createEmployeeWithStatus(employeeId1, "Joe", "Staff", null, EmploymentStatus.ACTIVE);
        createEmployeeWithStatus(employeeId2, "Sam", "Staff", null, EmploymentStatus.ACTIVE);

        List<Employee> employeesFound = employeeDAO.searchByNameEmailOrEmployeeId("Staff", 1);
        Assert.assertEquals("Staff", Iterables.getOnlyElement(employeesFound).getLastName());
    }

    @Test
    public void testSearchByNameOrEmplid_findCorrectEmployee()
    {
        EmployeeEntity employee1 = createEmployeeWithStatus(employeeId1, "Joseph", "Staff",
                "joe.staff@ccci.org", EmploymentStatus.ACTIVE);
        employee1.setPreferredFirstName("Joe");

        EmployeeEntity employee2 = createEmployeeWithStatus(employeeId2, "Samuel", "Intern",
                "sam.intern@uscm.org", EmploymentStatus.ACTIVE);
        employee2.setPreferredFirstName("Sam");

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
        createEmployeeWithStatus(employeeId1, "Joseph", "Staff", null, EmploymentStatus.ACTIVE);
        createEmployeeWithStatus(employeeId2, "Samuel", "Intern", "sam.intern@uscm.org", EmploymentStatus.ACTIVE);
        createEmployeeWithStatus(employeeId3, "Jane", "Staff", "jane.staff@ccci.org", EmploymentStatus.ACTIVE);
        
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
        createEmployeeWithStatus(employeeId1, "Joseph", "Staff", null, EmploymentStatus.ACTIVE);
        createEmployeeWithStatus(employeeId3, "Jane", "Staff", null, EmploymentStatus.ACTIVE);
        
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
        createEmployee(employeeId1, "Joe", "Staff", "joe.staff@cru.org");
        createEmployee(employeeId2, "Sam", "Intern", "sam.intern@cru.org");

        Set<PersonalEmailAddress> emailAddresses = employeeDAO.getPersonalEmailAddressesForIds(
            Sets.newHashSet(employeeId1, employeeId2));
        
        Assert.assertEquals(2, emailAddresses.size());
        assertContainsEmail(emailAddresses, "joe.staff@cru.org", "Joe Staff",
                "Expected to find Joe's email address");
        assertContainsEmail(emailAddresses, "sam.intern@cru.org", "Sam Intern",
                "Expected to find Sam's email address");
    }
    
    @Test
    public void testGetPersonalEmailAddressesForIds_filtersEmptyEmails()
    {
        createEmployee(employeeId1, "Joe", "Staff", "joe.staff@cru.org");
        createEmployee(employeeId2, "Sam", "Intern", null); // No email - should be filtered out

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
        // Set a small partition size to force chunking with our test data
        employeeDAO.setPartitionSize(2);
        
        // Test the partitioning logic directly
        Set<EmployeeId> employeeIds = Sets.newHashSet(employeeId1, employeeId2, employeeId3);
        List<List<EmployeeId>> partitions = employeeDAO.partition(employeeIds);
        
        // Verify that 3 employee IDs with partition size 2 creates exactly 2 partitions
        Assert.assertEquals("Expected 2 partitions due to chunking", 2, partitions.size());
        Assert.assertEquals("First partition should have 2 employees", 2, partitions.get(0).size());
        Assert.assertEquals("Second partition should have 1 employee", 1, partitions.get(1).size());
        
        // Test that the actual method still works correctly with chunking
        createEmployee(employeeId1, "Joe", "Staff", "joe.staff@cru.org");
        createEmployee(employeeId2, "Sam", "Intern", "sam.intern@cru.org");
        createEmployee(employeeId3, "Jane", "Manager", "jane.manager@cru.org");

        Set<PersonalEmailAddress> emailAddresses = employeeDAO.getPersonalEmailAddressesForIds(employeeIds);
        
        Assert.assertEquals(3, emailAddresses.size());
        
        // Verify all employees are found despite chunking
        assertContainsEmail(emailAddresses, "joe.staff@cru.org", "Joe Staff",
                "Expected to find Joe's email from first chunk");
        assertContainsEmail(emailAddresses, "sam.intern@cru.org", "Sam Intern",
                "Expected to find Sam's email from first chunk");
        assertContainsEmail(emailAddresses, "jane.manager@cru.org", "Jane Manager",
                "Expected to find Jane's email from second chunk");
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testGetPersonalEmailAddressesForIds_nullInput()
    {
        employeeDAO.getPersonalEmailAddressesForIds(null);
    }

    // Helper method for creating test employees with specific employment status
    private EmployeeEntity createEmployeeWithStatus(EmployeeId employeeId, String firstName, String lastName,
                                                    String email, String employmentStatus)
    {
        EmployeeEntity employee = createEmployee(employeeId, firstName, lastName, email);
        employee.setEmploymentStatus(employmentStatus);
        return employee;
    }

    // Helper method for creating test employees - no employment status set automatically
    private EmployeeEntity createEmployee(EmployeeId employeeId, String firstName, String lastName, String email)
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
    
    // Helper method for verifying email addresses in results
    private void assertContainsEmail(Set<PersonalEmailAddress> emailAddresses, String expectedEmail, String expectedName,
                                     String description)
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
}
