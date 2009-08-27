package org.ccci.dao;

import java.util.Set;

import org.ccci.model.EmployeeId;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public class MultipleEmployeesFoundException extends RuntimeException
{
    
    private final Set<EmployeeId> employeeIds;

    /**
     * @param employeeIds
     *            must contain at least two employee ids. Does not necessarily contain <b>all</b> matches; just at
     *            least 2. Can be used to construct user messages if need be.
     */
    public MultipleEmployeesFoundException(Set<EmployeeId> employeeIds)
    {
        this.employeeIds = check(employeeIds);
    }

    private Set<EmployeeId> check(Set<EmployeeId> employeeIdsArg)
    {
        Preconditions.checkNotNull(employeeIdsArg, "employeeIds is null");
        Preconditions.checkArgument(employeeIdsArg.size() >= 2, "employeeIds contains fewer than 2 elements");
        return ImmutableSet.copyOf(employeeIdsArg);
    }

    public MultipleEmployeesFoundException(String message, Throwable cause, Set<EmployeeId> employeeIds)
    {
        super(message, cause);
        this.employeeIds = check(employeeIds);
    }

    public MultipleEmployeesFoundException(String message, Set<EmployeeId> employeeIds)
    {
        super(message);
        this.employeeIds = check(employeeIds);
    }

    public MultipleEmployeesFoundException(Throwable cause, Set<EmployeeId> employeeIds)
    {
        super(cause);
        this.employeeIds = check(employeeIds);
    }

    /**
     * An immutable set of some the employeeIds of the employees found by the search that caused this exception.
     * The set will contain at least two EmployeeIds, but not necessarily all of the employees that match (for
     * performance reasons; we don't want an overly broad search to return a list of half the employees in the
     * database).
     * 
     * @return
     */
    public Set<EmployeeId> getEmployeeIds()
    {
        return employeeIds;
    }

    private static final long serialVersionUID = 1L;

}
