package org.ccci.model;

import org.ccci.util.mail.EmailAddress;
import org.ccci.util.mail.PersonalEmailAddress;
import org.joda.time.LocalDate;

/**
 * Implementors should implement a toString() that contains the employee's id and name
 * @author Matt Drees
 *
 */
public interface Employee
{

    /**
     * Should never return null
     */
    public EmployeeId getEmployeeId();

    /**
     * See {@link PayGroup}
     * @return a valid PayGroup
     */
    public String getPayGroup();

    /**
     * Should return a 2-digit US state code
     */
    public String getState();

    /**
     * See {@link EmploymentStatus}
     */
    public String getEmploymentStatus();

    public String getFirstName();

    public String getLastName();
    
    public boolean isHourly();
    
    public String getDepartmentId();
    
    public String getDepartmentName();

    public EmailAddress getEmail();
    
    public PersonalEmailAddress getPersonalEmail();

    public String getPhone();

    public boolean isMarried();

    public LocalDate getTerminationDate();

    public boolean isIntern();
    
    /**
     * Returns the concatenation of first and last name, separated by a space
     */
    public String getName();

    public String getReportsTo();
    
    public String getSupervisorId();
    
    public String getSupervisorName();
    
    public String getMinistry();
    
    public String getSubMinistry();

    public Gender getGender();
    
    public String getTaxLocationCd();
}
