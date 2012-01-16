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
