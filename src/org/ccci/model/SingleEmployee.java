package org.ccci.model;

import java.util.Collections;
import java.util.Iterator;

import com.google.common.base.Preconditions;

/**
 * Represents a single employee, or a married employee whose spouse is not in HR.
 * In rare cases, the employee's id may be a secondary id (one case would be a widow).
 * 
 * @author Matt Drees
 *
 */
public class SingleEmployee implements EmployeeUnit
{

    private final Employee employee;

    public SingleEmployee(Employee employee)
    {
        Preconditions.checkNotNull(employee, "employee is null");
        this.employee = employee;
    }

    @Override
    public String getName()
    {
        return employee.getName();
    }

    @Override
    public Employee getPrimary()
    {
        return employee;
    }

    @Override
    public boolean isCouple()
    {
        return false;
    }

    @Override
    public Iterator<Employee> iterator()
    {
        return Collections.singleton(employee).iterator();
    }
    
    private static final long serialVersionUID = 1L;
}
