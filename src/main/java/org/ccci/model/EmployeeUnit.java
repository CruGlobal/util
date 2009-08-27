package org.ccci.model;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Represents either a single employee, or a married couple (both employees).
 * The abstraction is useful because several tools deal with couples as a unit, not as individuals.
 * This promotes reuse between code that processes a married couple and a single employee.
 * 
 * @author Matt Drees
 *
 */
public interface EmployeeUnit extends Iterable<Employee>, Serializable
{

    boolean isCouple();

    /**
     * Returns the primary employee in this unit.  For singles, returns the employee.
     * For couples, returns the employee with a primary employeeId (see {@link EmployeeId#isPrimaryId()}).
     * Usually, though not always, this is the male.  
     * @return
     */
    Employee getPrimary();

    /**
     * Returns a reasonable name representing the employee(s)
     * e.g. "John Smith" for singles and "John and Jane Smith" for couples
     */
    String getName();

    /**
     * Returns an iterator of the employee(s).  
     */
    @Override
    Iterator<Employee> iterator();
}
