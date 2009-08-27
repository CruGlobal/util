package org.ccci.dao.psdb;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.ccci.dao.EmployeeDAO;
import org.ccci.dao.EmployeeNotFoundException;
import org.ccci.dao.MultipleEmployeesFoundException;
import org.ccci.debug.RecordExceptions;
import org.ccci.model.Employee;
import org.ccci.model.EmployeeCouple;
import org.ccci.model.EmployeeId;
import org.ccci.model.EmployeeUnit;
import org.ccci.model.EmploymentStatus;
import org.ccci.model.SingleEmployee;
import org.ccci.util.CollectionsExtension;
import org.ccci.util.Exceptions;
import org.ccci.util.Generics;
import org.ccci.util.mail.PersonalEmailAddress;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.google.common.base.Preconditions;
import com.google.common.collect.Comparators;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Likely will be replaced by a webservice call to peoplesoft
 * 
 * Requires the availability of a persistence context called psEntityManager with access to the
 * peoplesoft schema.
 * 
 * Directly reads from the ps_employees2 table, instead of from the webpsempl2 view.
 * 
 * @author Matt Drees
 *
 */
@Name("employeeDAO")
@Scope(ScopeType.STATELESS)
@RecordExceptions
@AutoCreate
public class EmployeeDAOImpl implements EmployeeDAO
{

    @Logger Log log;
    
    @In EntityManager psEntityManager;

    private int partitionSize = 1000; //oracle cannot handle more than 1000 expressions in a " IN (...)" list clause
    

    /*
     * We only look at empld_rcd 0.  Usually, employees only have one record.  One case where they
     * have two is if they are a hourly staff moving to supported staff, in which case they'd have
     * a record for each.
     */
    @Override
    public Employee find(EmployeeId employeeId) throws EmployeeNotFoundException
    {
        Preconditions.checkNotNull(employeeId, "employeeId is null");
        return findInternal(employeeId);
    }

    private Employee findInternal(EmployeeId employeeId)
    {
        EmployeeEntity employeeEntity = psEntityManager.find(EmployeeEntity.class, 
            new EmployeeEntity.Key(employeeId, 0));
        if (employeeEntity == null)
        {
            throw new EmployeeNotFoundException("No employee exists with employee id " + employeeId);
        }
        return employeeEntity;
    }
    
    @Override
    public Map<EmployeeId, Employee> find(Set<EmployeeId> employeeIds) throws EmployeeNotFoundException
    {
        Preconditions.checkNotNull(employeeIds, "employeeIds is null");
        if (employeeIds.isEmpty())
        {
            return Collections.emptyMap();
        }
        
        List<Employee> foundEmployees = findInternal(employeeIds);
        
        if (foundEmployees.size() < employeeIds.size())
        {
            List<EmployeeId> foundEmployeeIds = Lists.newArrayList();
            for (Employee employee : foundEmployees)
            {
                foundEmployeeIds.add(employee.getEmployeeId());
            }
            Set<EmployeeId> difference = CollectionsExtension.difference(Sets.newHashSet(employeeIds), Sets.newHashSet(foundEmployeeIds));
            throw new EmployeeNotFoundException(String.format("no employee data found for id(s): %s", difference));
        }
        
        sortFoundEmployees(foundEmployees, Lists.newArrayList(employeeIds));
        return createdOrderedEmployeeMap(foundEmployees);
    }

    @Override
    public Map<EmployeeId, Employee> lenientFind(Set<EmployeeId> employeeIds)
    {
        Preconditions.checkNotNull(employeeIds, "employeeIds is null");
        if (employeeIds.isEmpty())
        {
            return Collections.emptyMap();
        }

        List<Employee> foundEmployees = findInternal(employeeIds);
        sortFoundEmployees(foundEmployees, Lists.newArrayList(employeeIds));
        return createdOrderedEmployeeMap(foundEmployees);
    }
    
    private List<Employee> findInternal(final Set<EmployeeId> employeeIds)
    {
        List<Employee> employees = Generics.checkList(Employee.class, 
            buildFindQuery(employeeIds)
            .getResultList());
        
        if (employees.size() > employeeIds.size())
        {
            throw Exceptions.newIllegalStateException("query result list is larger than passed in list of employeeIds; %s vs %s", 
                employees.size(), employeeIds.size());
        }
        return employees;
    }

    private Query buildFindQuery(final Set<EmployeeId> employeeIds)
    {
        List<List<EmployeeId>> partition = partition(employeeIds);
        int numParts = partition.size();
        
        StringBuilder findQuery = new StringBuilder("select e from EmployeeEntity e where (");
        for (int i = 0; i < numParts; i++)
        {
            findQuery.append("(e.key.employeeId in (:employeeIds").append(i).append("))");
            if (i != numParts - 1)
            {
                findQuery.append(" or ");
            }
        }
        findQuery.append(") and e.key.employeeRecord = 0");
        Query query = psEntityManager.createQuery(findQuery.toString());
        int i = 0;
        for (List<EmployeeId> part : partition)
        {
            query.setParameter("employeeIds" + i, part);
            i++;
        }
        
        return query;
    }

    private List<List<EmployeeId>> partition(final Set<EmployeeId> employeeIds)
    {
        Iterable<Iterable<EmployeeId>> partitionAsIterable = Iterables.partition(employeeIds, partitionSize, false);
        List<List<EmployeeId>> partition = Lists.newArrayList();
        for (Iterable<EmployeeId> partAsIterable : partitionAsIterable)
        {
            List<EmployeeId> part = Lists.newArrayList(partAsIterable);
            partition.add(part);
        }
        return partition;
    }
    
    /*
        select e from EmployeeEntity e where 
        (e.key.employeeId in (:employeeIds1)) or
        (e.key.employeeId in (:employeeIds2))
        and e.key.employeeRecord = 0
     */
    

    /** sort the employee list according to the given employee id list */
    private void sortFoundEmployees(List<Employee> employees, final List<EmployeeId> employeeIds)
    {
        Collections.sort(employees, new Comparator<Employee>() {
            @Override
            public int compare(Employee employee1, Employee employee2)
            {
                return Comparators.compare(employeeIds.indexOf(employee1.getEmployeeId()),
                    employeeIds.indexOf(employee2.getEmployeeId()));
            }
        });
    }
    
    private Map<EmployeeId, Employee> createdOrderedEmployeeMap(List<Employee> foundEmployees)
    {
        Map<EmployeeId, Employee> employeeMap = new LinkedHashMap<EmployeeId, Employee>(foundEmployees.size());
        for (Employee employee : foundEmployees)
        {
            employeeMap.put(employee.getEmployeeId(), employee);
        }
        return employeeMap;
    }
    
    
    @Override
    public List<Employee> searchByNameEmailOrEmployeeId(String searchString, Integer maxResults)    
    {
        Preconditions.checkNotNull(searchString, "searchString is null");
        Preconditions.checkArgument(!searchString.isEmpty(), "searchString is empty");
    	return searchByNameEmailOrEmployeeIdInternal(searchString, maxResults);
    }

    private List<Employee> searchByNameEmailOrEmployeeIdInternal(String searchString, Integer maxResults)
    {
        List<String> terms = gatherTerms(searchString);
    	StringBuilder queryBuilder = new StringBuilder(
    	    "select e from EmployeeEntity e" + 
            " where e.employmentStatus = :active" + 
            " and e.key.employeeRecord = 0 ");
    	String restrictionFormat = 
    	    "and (e.firstNameSearch like :%1$s" +
			" or e.lastNameSearch like :%1$s" +
			" or upper(e.preferredFirstName) like :%1$s" +
			" or upper(e.emailAddress) like :%1$s" +
			" or e.key.employeeId.employeeId like :%1$s)";

    	for (int i = 0; i < terms.size(); i++)
    	{
	        queryBuilder.append(String.format(restrictionFormat, toParameterName(i)));
    	}
    	
    	Query query = psEntityManager.createQuery(queryBuilder.toString());
    	query.setParameter("active", EmploymentStatus.ACTIVE);
    	for (int i = 0; i < terms.size(); i++)
    	{
    	    query.setParameter(toParameterName(i), "%" + terms.get(i).toUpperCase() + "%");
    	}
    	
    	if (maxResults != null) query.setMaxResults(maxResults);
        
    	return Generics.checkList(Employee.class, query.getResultList());
    }

    private String toParameterName(int i)
    {
        return "term" + i;
    }

	private List<String> gatherTerms(String searchString)
    {
	    return Arrays.asList(searchString.split("[\\s,.\\(\\)]+"));
    }

    @Override
	public Set<PersonalEmailAddress> getPersonalEmailAddressesForIds(Set<EmployeeId> employeeIds) 
	{
        Preconditions.checkNotNull(employeeIds, "employeeIds is null");
        if (employeeIds.isEmpty())
        {
            return Sets.newHashSet();
        }

        List<Object[]> rawEmailAddresses = Generics.checkObjectArrayList( 
            psEntityManager.createNamedQuery("EmployeeEntity.findEmailInfoByEmployeeIds")
            .setParameter("employeeIds", employeeIds)
            .getResultList(), 
            String.class, String.class, String.class);

        Set<PersonalEmailAddress> personalEmailAddresses = Sets.newHashSetWithExpectedSize(rawEmailAddresses.size());
        for (Object[] row : rawEmailAddresses)
        {
            String personalName = row[0] + " " + row[1];
            String emailAddress = (String) row[2];
            try
            {
                personalEmailAddresses.add(PersonalEmailAddress.newPersonalEmailAddress(emailAddress, personalName));
            }
            catch (IllegalArgumentException e)
            {
                log.warn("employee #0 has an invalid email address: #1", personalName, emailAddress);
            }
        }
        return personalEmailAddresses;
	}

    @Override
    public Employee getByNameEmailOrEmployeeId(String searchString) throws EmployeeNotFoundException,
            MultipleEmployeesFoundException
    {
        Preconditions.checkNotNull(searchString, "searchString is null");
        Preconditions.checkArgument(!searchString.isEmpty(), "searchString is empty");
        if (EmployeeId.isValidEmployeeId(searchString))
        {
            return findInternal(EmployeeId.valueOf(searchString));
        }
        int maxResults = 5; //should be more than enough to construct a useful message
        List<Employee> employees = searchByNameEmailOrEmployeeIdInternal(searchString, maxResults);
        if (employees.isEmpty())
        {
            throw new EmployeeNotFoundException("No such employee: " + searchString);
        }
        if (employees.size() > 1)
        {
            throw new MultipleEmployeesFoundException("More than one employee matches " + searchString, getIds(employees));
        }
        return Iterables.getOnlyElement(employees);
    }

    private Set<EmployeeId> getIds(List<Employee> employees)
    {
        Set<EmployeeId> employeeIds = Sets.newHashSet();
        for (Employee employee : employees)
        {
            employeeIds.add(employee.getEmployeeId());
        }
        return employeeIds;
    }

    @Override
    public EmployeeUnit findEmployeeUnit(EmployeeId employeeId)
    {
        Preconditions.checkNotNull(employeeId, "employeeId is null");
        Employee employee = findInternal(employeeId);
        if (!employee.isMarried()) 
            // as of the time of this writing, there exist some employees with marital status of single and yet have
            // a spouse; some spouse information in their employee record such as spouse_name is available. Since these cases are rare and really should be corrected
            // at the data level, I'm ignoring them here and treating them as singles.
        {
            return new SingleEmployee(employee);
        }
        
        if (employeeId.isPrimaryId())
        {
            return getEmployeeUnitInternal(employee, employeeId.getSpouseId());
        }
        else
        {
            return getEmployeeUnitInternal(employee, employeeId.getPrimaryId());
        }
    }
    
    private EmployeeUnit getEmployeeUnitInternal(Employee employee, EmployeeId otherEmployeeId)
    {
        try
        {
            Employee otherEmployee = findInternal(otherEmployeeId);
            if (otherEmployee.getEmployeeId().isPrimaryId())
            {
                return new EmployeeCouple(otherEmployee, employee);
            }
            else
            {
                return new EmployeeCouple(employee, otherEmployee);
            }
        }
        catch (EmployeeNotFoundException e)
        {
            return new SingleEmployee(employee);
        }
    }

    public int getPartitionSize()
    {
        return partitionSize;
    }

    public void setPartitionSize(int partitionSize)
    {
        Preconditions.checkArgument(partitionSize > 0, "partitionSize is not positive: %s", partitionSize);
        this.partitionSize = partitionSize;
    }
    
}
