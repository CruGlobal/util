package org.ccci.dao.psdb;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.Table;

import org.ccci.annotations.JpaConstructor;
import org.ccci.model.Employee;
import org.ccci.model.EmployeeId;
import org.ccci.model.Gender;
import org.ccci.model.MarriageStatus;
import org.ccci.model.PayGroup;
import org.ccci.util.ValueObject;
import org.ccci.util.mail.EmailAddress;
import org.ccci.util.mail.PersonalEmailAddress;
import org.ccci.util.strings.ToStringBuilder;
import org.ccci.util.strings.ToStringProperty;
import org.ccci.util.time.TimeUtil;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.joda.time.LocalDate;

/**
 * Likely will be replaced by a webservice call to peoplesoft
 * 
 * @author Matt Drees
 * 
 */
@Entity
@Table(name = "PS_EMPLOYEES2")
@NamedQueries({
    @NamedQuery(
        name="EmployeeEntity.findByNameOrEmplId",
		query=" select e from EmployeeEntity e"
			+ " where e.employmentStatus = 'A'"
            + " and e.key.employeeRecord = 0"
			+ " and (upper(e.lastName) like upper(:lastName) "
			+ "  or upper(e.firstName) like  upper(:firstName) "
			+ "  or e.key.employeeId.employeeId like (:emplId))" ),
	@NamedQuery(
	    name="EmployeeEntity.findByEmployeeIds",
	    query=" select e from EmployeeEntity e"
	        + " where e.key.employeeId in (:employeeIds)"
	        + " and e.key.employeeRecord = 0"),
    @NamedQuery(
		name="EmployeeEntity.findEmailInfoByEmployeeIds",
		query=" select e.firstName, e.lastName, e.emailAddress from EmployeeEntity e"
			+ " where e.key.employeeId in (:employeeIds)"
            + " and e.key.employeeRecord = 0" 
			+ " and length(trim(e.emailAddress)) > 0")
})

public class EmployeeEntity implements Employee, Serializable
{
    private static final long serialVersionUID = 1L;

    @Embeddable
    public static class Key extends ValueObject implements Serializable
    {

        public Key(EmployeeId employeeId, int employeeRecord)
        {
            this.employeeId = employeeId;
            this.employeeRecord = employeeRecord;
        }

        @JpaConstructor
        protected Key()
        {
        }

        @Embedded
        @AttributeOverride(name = "employeeId", column =
            @Column(name = "EMPLID", nullable = false, updatable = false, length = 11))
        private EmployeeId employeeId;

        /**
         * Oddly, ps_employees.EMPL_RCD is a NUMBER, not specifying a precision or scale, so theoretically could be
         * fractional. In practice, it never is; it's a whole number not less than 0.
         */
        @Column(name = "EMPL_RCD", nullable = false, updatable = false)
        private double employeeRecord;

        private static Log log = Logging.getLog(Key.class);

        @PostLoad
        void checkEmployeeRecord()
        {
            int employeeRecordAsInt = (int) employeeRecord;
            if (employeeRecord != (double) employeeRecordAsInt) //probably won't ever happen
            {
                log.warn("non-integral employee record #0 for employee id #1", employeeRecord, employeeId);
                //but shouldn't affect code much...
            }
        }

        @Override
        protected Object[] getComponents()
        {
            return new Object[]{employeeId, employeeRecord};
        }

        public EmployeeId getEmployeeId()
        {
            return employeeId;
        }

        public int getEmployeeRecord()
        {
            return (int) employeeRecord;
        }

        private static final long serialVersionUID = 1L;
    }
    
    @EmbeddedId
    private Key key;
    
    @Column(name = "EMPL_STATUS")
    private String employmentStatus;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "PREF_FIRST_NAME")
    private String preferredFirstName;
    
    @Column(name = "LAST_NAME")
    private String lastName;

    /** like {@link #firstName}, but capitalized for faster searching (I think)*/
    @SuppressWarnings("unused") //used only by JPA queries
    @Column(name = "FIRST_NAME_SRCH")
    private String firstNameSearch;
    
    /** like {@link #lastName}, but capitalized for faster searching (I think)*/
    @SuppressWarnings("unused") //used only by JPA queries
    @Column(name = "LAST_NAME_SRCH")
    private String lastNameSearch;
    
    private String payGroup;

    @Column(name = "HOLIDAY_SCHEDULE")
    private String holidaySchedule;

    private String state;
    
    @Column(name = "DEPTID")
    private String departmentId;
    
    @Column(name = "DEPTNAME")
    private String departmentName;
    
    @Column(name = "PHONE")
    private String phone;

    @Column(name = "TERMINATION_DT")
    private Date terminationDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "MAR_STATUS", length = 1)
    private MarriageStatus marriedStatus;

    /** note: database may contain invalid email addresses */
    @Column(name = "EMAIL_ADDR", updatable = false, length = 70)
    private String emailAddress;

    @Column(name = "SUPERVISOR_ID")
    private String supervisorId;
    
    @Column(name = "SUPERVISOR_NAME")
    private String supervisorName;
    
    @Column(name = "REPORTS_TO")
    private String reportsTo;
    
    @Column(name = "CCC_MINISTRY")
    private String ministry;
    
    @Column(name = "CCC_SUB_MINISTRY")
    private String subMinistry;
    
    @Column(name = "TAX_LOCATION_CD")
    private String taxLocationCd;

    @Enumerated(EnumType.STRING)
    @Column(name = "SEX")
    private Gender gender;

    @Override
    public String toString()
    {
        return new ToStringBuilder(this).toString(); 
    }

    @Override
    public boolean isHourly()
    {
        return PayGroup.isHourly(payGroup);
    }

    @Override
    public boolean isMarried()
    {
        return MarriageStatus.M.equals(marriedStatus);
    }

    /**
     * Returns this employee's email address. If the email address is null, empty, or unparseable, returns null.
     */
    @Override
    public EmailAddress getEmail()
    {
        try
        {
            return emailAddress == null ? null : EmailAddress.valueOf(emailAddress);
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    /**
     * Returns a {@link PersonalEmailAddress}, constructed by {@link #getEmail()} and {@link #getName()}. If the
     * email address is null, empty, or unparseable, returns null.
     */
    @Override
    public PersonalEmailAddress getPersonalEmail()
    {
        try
        {
            return getEmail() == null ? null : PersonalEmailAddress.newPersonalEmailAddress(getEmail(), getName());
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    
    /*
     * Straight getters & setters
     */
    

    @Override
    @ToStringProperty
    public EmployeeId getEmployeeId()
    {
        return key == null ? null : key.getEmployeeId();
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

    @Override
    public String getLastName()
    {
        return lastName;
    }

    @Override
    public String getPayGroup()
    {
        return payGroup;
    }

    @Override
    public String getHolidaySchedule()
    {
        return holidaySchedule;
    }

    @Override
    public String getState()
    {
        return state;
    }

    @Override
    public String getDepartmentId()
    {
        return departmentId;
    }

    @Override
    public String getDepartmentName()
    {
        return departmentName;
    }

    @Override
    public LocalDate getTerminationDate()
    {
        return TimeUtil.sqlDateToLocalDate(terminationDate);
    }
    
    public void setDepartmentName(String departmentName)
    {
        this.departmentName = departmentName;
    }

    @Override
    public String getPhone()
    {
        return phone;
    }
    
    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public MarriageStatus getMarriedStatus()
    {
        return marriedStatus;
    }

    public void setMarriedStatus(MarriageStatus marriedStatus)
    {
        this.marriedStatus = marriedStatus;
    }

    public void setKey(Key key)
    {
        this.key = key;
    }

    public void setEmploymentStatus(String employmentStatus)
    {
        this.employmentStatus = employmentStatus;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
        this.firstNameSearch = firstName == null ? null : firstName.toUpperCase();
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
        this.lastNameSearch = lastName == null ? null : lastName.toUpperCase();
    }

    public void setPayGroup(String payGroup)
    {
        this.payGroup = payGroup;
    }

    public void setHolidaySchedule(String holidaySchedule)
    {
        this.holidaySchedule = holidaySchedule;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public void setDepartmentId(String departmentId)
    {
        this.departmentId = departmentId;
    }

    public void setEmail(EmailAddress email)
    {
        this.emailAddress = email.toString();
    }

    public void setTerminationDate(LocalDate terminationDate)
    {
        this.terminationDate = TimeUtil.localDateToSqlDate(terminationDate);
    }

    @Override
    public boolean isIntern()
    {
        return PayGroup.isIntern(payGroup);
    }

    @Override
    @ToStringProperty
    public String getName()
    {
        return firstName + " " + lastName;
    }

    @Override
    public String getSupervisorId()
    {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId)
    {
        this.supervisorId = supervisorId;
    }

    @Override
    public String getSupervisorName()
    {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName)
    {
        this.supervisorName = supervisorName;
    }

    @Override
    public String getReportsTo()
    {
        return reportsTo;
    }

    public void setReportsTo(String reportsTo)
    {
        this.reportsTo = reportsTo;
    }

    @Override
    public String getMinistry()
    {
        return ministry;
    }

    public void setMinistry(String ministry)
    {
        this.ministry = ministry;
    }

    @Override
    public String getSubMinistry()
    {
        return subMinistry;
    }

    public void setSubMinistry(String subMinistry)
    {
        this.subMinistry = subMinistry;
    }

    public void setTerminationDate(Date terminationDate)
    {
        this.terminationDate = terminationDate;
    }

    @Override
    public Gender getGender()
    {
        return gender;
    }

    public void setGender(Gender gender)
    {
        this.gender = gender;
    }

    public String getPreferredFirstName()
    {
        return preferredFirstName;
    }

    public void setPreferredFirstName(String preferredFirstName)
    {
        this.preferredFirstName = preferredFirstName;
    }

	public String getTaxLocationCd() {
		return taxLocationCd;
	}

	public void setTaxLocationCd(String taxLocationCd) {
		this.taxLocationCd = taxLocationCd;
	}

}
