package org.ccci.dao.mock;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ccci.dao.EmployeeDAO;
import org.ccci.dao.EmployeeNotFoundException;
import org.ccci.dao.MultipleEmployeesFoundException;
import org.ccci.model.Employee;
import org.ccci.model.EmployeeId;
import org.ccci.model.EmployeeUnit;
import org.ccci.util.NotImplementedException;
import org.ccci.util.mail.PersonalEmailAddress;

public class EmptyEmployeeDAO implements EmployeeDAO
{

    @Override
    public Employee find(EmployeeId employeeId) throws EmployeeNotFoundException
    {
        throw new NotImplementedException();
    }

    @Override
    public List<Employee> searchByNameEmailOrEmployeeId(String searchString, Integer maxResults)
    {
        throw new NotImplementedException();
    }

    @Override
    public Map<EmployeeId, Employee> find(Set<EmployeeId> employeeIds) throws EmployeeNotFoundException
    {
        throw new NotImplementedException();
    }

    @Override
    public Map<EmployeeId, Employee> lenientFind(Set<EmployeeId> employeeIds)
    {
        throw new NotImplementedException();
    }

	@Override
	public Set<PersonalEmailAddress> getPersonalEmailAddressesForIds(Set<EmployeeId> employeeIds) 
	{
        throw new NotImplementedException();
	}

    @Override
    public Employee getByNameEmailOrEmployeeId(String searchString) throws EmployeeNotFoundException,
            MultipleEmployeesFoundException
    {
        throw new NotImplementedException();
    }

    @Override
    public EmployeeUnit findEmployeeUnit(EmployeeId employeeId)
    {
        throw new NotImplementedException();
    }

	@Override
	public String loadTaxState(String taxLocationCd) 
	{
		throw new NotImplementedException();
	}

}
