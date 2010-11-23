package org.ccci.model;

import java.io.Serializable;

import org.ccci.util.mail.EmailAddress;
import org.ccci.util.mail.PersonalEmailAddress;
import org.joda.time.LocalDate;

/**
 * Useful for tests and mocks
 * 
 * @author Matt Drees
 *
 */
public class EmployeePojo implements Employee, Serializable
{

    private EmployeeId employeeId;
    
    private String payGroup;
    
    private String state;
    
    private String employmentStatus;

    private String firstName;
    
    private String lastName;

    private EmailAddress email;
    
    private boolean isHourly;
    
    private String departmentId;

    private String departmentName;
    
    private String phone;
    
    private String marriedStatus;
    
    private LocalDate terminationDate;

    private String reportsTo;
    
    private String ministry;
    
    private String subMinistry;
    
    private String supervisorId;
    
    private String supervisorName;
    
    private Gender gender;
    
    private String taxLocationCd;
    
    public EmployeePojo(EmployeeId emplid)
    {
        this.employeeId = emplid;
        setEmploymentStatus(EmploymentStatus.ACTIVE);
        isHourly = true;
    }

    public EmployeePojo(EmployeeId emplid, String firstName, String lastName)
    {
        this.employeeId = emplid;
        this.firstName = firstName;
        this.lastName = lastName;
        setEmploymentStatus(EmploymentStatus.ACTIVE);
        isHourly = true;
    }

    @Override
    public EmployeeId getEmployeeId()
    {
        return employeeId;
    }

    public void setEmployeeId(EmployeeId employeeId)
    {
        this.employeeId = employeeId;
    }

    @Override
    public String getPayGroup()
    {
        return payGroup;
    }

    @Override
    public String getState()
    {
        return state;
    }

    @Override
    public String getEmploymentStatus()
    {
        return employmentStatus;
    }

    @Override
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    @Override
    public String getLastName()
    {
        return lastName;
    }
    
    @Override
    public EmailAddress getEmail()
    {
        return email;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public void setPayGroup(String payGroup)
    {
        this.payGroup = payGroup;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public void setEmploymentStatus(String employmentStatus)
    {
        this.employmentStatus = employmentStatus;
    }

    public boolean isHourly()
    {
        return isHourly;
    }

    public void setHourly(boolean isHourly)
    {
        this.isHourly = isHourly;
    }

    public String getDepartmentId()
    {
        return departmentId;
    }

    public void setDepartmentId(String departmentId)
    {
        this.departmentId = departmentId;
    }


    public void setEmail(EmailAddress email)
    {
        this.email = email;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getMarriedStatus()
    {
        return marriedStatus;
    }

    public void setMarriedStatus(String marriedStatus)
    {
        this.marriedStatus = marriedStatus;
    }

    public LocalDate getTerminationDate()
    {
        return terminationDate;
    }

    public void setTerminationDate(LocalDate terminationDate)
    {
        this.terminationDate = terminationDate;
    }

    @Override
    public boolean isMarried()
    {
        return marriedStatus == "M";
    }

    public boolean isIntern()
    {
        return PayGroup.isIntern(payGroup);
    }

    @Override
    public String getName()
    {
        return firstName + " " + lastName;
    }

    public String getDepartmentName()
    {
        return departmentName;
    }

    public void setDepartmentName(String deptName)
    {
        this.departmentName = deptName;
    }

    public String getReportsTo()
    {
        return reportsTo;
    }

    public void setReportsTo(String reportsTo)
    {
        this.reportsTo = reportsTo;
    }

    public String getMinistry()
    {
        return ministry;
    }

    public void setMinistry(String ministry)
    {
        this.ministry = ministry;
    }

    public String getSubMinistry()
    {
        return subMinistry;
    }

    public void setSubMinistry(String subMinistry)
    {
        this.subMinistry = subMinistry;
    }

    public String getSupervisorId()
    {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId)
    {
        this.supervisorId = supervisorId;
    }

    public String getSupervisorName()
    {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName)
    {
        this.supervisorName = supervisorName;
    }

    public Gender getGender()
    {
        return gender;
    }

    public void setGender(Gender gender)
    {
        this.gender = gender;
    }

    @Override
    public PersonalEmailAddress getPersonalEmail()
    {
        return PersonalEmailAddress.newPersonalEmailAddress(email, getName());
    }

    private static final long serialVersionUID = 1L;

	public String getTaxLocationCd() {
		return taxLocationCd;
	}

	public void setTaxLocationCd(String taxLocationCd) {
		this.taxLocationCd = taxLocationCd;
	}

}
