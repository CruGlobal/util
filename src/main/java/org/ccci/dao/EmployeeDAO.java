package org.ccci.dao;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ccci.model.Employee;
import org.ccci.model.EmployeeCouple;
import org.ccci.model.EmployeeId;
import org.ccci.model.EmployeeUnit;
import org.ccci.model.SingleEmployee;
import org.ccci.util.mail.PersonalEmailAddress;

/**
 * Interacts with peoplesoft to provide standard Employee information
 * @author Matt Drees
 *
 */
public interface EmployeeDAO
{

    /**
     * @param employeeId
     * @return the Employee with the given employeeId.  Will not return null.
     * @throws EmployeeNotFoundException if the employeeId given does not exist
     */
    Employee find(EmployeeId employeeId) throws EmployeeNotFoundException;

    /**
     * Like {@link #find(Set)}, but if any employeeId does not exist, it is ignored instead of triggering an
     * {@link EmployeeNotFoundException}.
     * 
     * @param employeeIds a set of ids for which to search
     * @return  See note on {@link #find(Set)}
     */
    Map<EmployeeId, Employee> lenientFind(Set<EmployeeId> employeeIds);

    /**
     * Returns a map containing the {@link Employee} objects corresponding to the given employee ids.
     * 
     * @param employeeIds
     *            a set of ids for which to search
     * @return an ordered map which maps the given employee ids to the corresponding Employee objects. The map is
     *         ordered according to the iteration order of the given set (see {@link LinkedHashMap} for semantics).
     *         So if order is important, pass in e.g. a {@link LinkedHashSet}.  Will not contain null values or keys.
     * @throws EmployeeNotFoundException
     *             if any of the given employeeIds do not exist
     */
    Map<EmployeeId, Employee> find(Set<EmployeeId> employeeIds) throws EmployeeNotFoundException;

    /**
     * Returns a list of employees that match the given searchString. Searches are performed against first name,
     * last name, preferred first name, email, and employee id. Each term in the given searchString must match at
     * least one of these fields.
     * 
     * Only searches for active employees.
     * 
     * @param searchString
     *            a string of terms to search for. Input should be trimmed. Must not be null or empty.
     * @param maxResults
     *            maximum number of results to return. If null, return all results.
     * @return a list of matching employees
     * @throws NullPointerException
     *             if searchString is null
     * @throws IllegalArgumentException
     *             if searchString is empty
     */
    List<Employee> searchByNameEmailOrEmployeeId(String searchString, Integer maxResults);

    /**
     * Returns a unique employee that matches the given {@code searchString}. If the given {@code searchString} is
     * a valid employee id, return the employee that owns the given employee id; if no employee exists with the
     * given employee id, an {@link EmployeeNotFoundException} is thrown. If the given {@code searchString} is not
     * an employee id, a search is performed with the same semantics as
     * {@link #searchByNameEmailOrEmployeeId(String, Integer)}. If there is more than one result returned, a
     * MultipleEmployeesFoundException is thrown. If no result is returned, a {@link EmployeeNotFoundException} is
     * thrown.
     * 
     * @param searchString
     *            either an employee id, or a string of terms to search for. Input should be trimmed. Must not be
     *            null or empty.
     * @return a non-null, unique Employee
     * @throws EmployeeNotFoundException
     *             if the given searchString does not correspond to any employee
     * @throws MultipleEmployeesFoundException
     *             if the given searchString corresponds to multiple employees
     * @throws NullPointerException if searchString is null
     * @throws IllegalArgumentException if searchString is empty
     */
    Employee getByNameEmailOrEmployeeId(String searchString) throws EmployeeNotFoundException,
            MultipleEmployeesFoundException;

    /**
     * Returns a {@link Set} of {@link PersonalEmailAddress}s for each employeeId passed, if it exists. If a given
     * employee id refers to an employee that has no email or an invalid email address, no corresponding
     * PersonalEmailAddress will be returned.
     * 
     * FUTURE: a more useful api would return a Map<EmployeeID, PersonalEmailAddress>
     * 
     * @param employeeIds
     * @return
     */
	Set<PersonalEmailAddress> getPersonalEmailAddressesForIds(Set<EmployeeId> employeeIds);

	/**
	 * Returns an appropriate {@link EmployeeUnit} for the given {@link EmployeeId}.  
	 * If the corresponding employee is married and the spouse exists in HR, a {@link EmployeeCouple} is
	 * returned.
	 * Otherwise, a {@link SingleEmployee} is returned.
	 * @param employeeId
	 * @throws EmployeeNotFoundException if the given employeeId does not exist
	 */
    EmployeeUnit findEmployeeUnit(EmployeeId employeeId);
    
	/**
     * Returns the Tax State as String
     * @return
     */
	String loadTaxState(String taxLocationCd);
    

}
