package org.ccci.model;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Embeddable;
import javax.persistence.PostLoad;

import org.ccci.util.ConstructsFromString;
import org.ccci.util.ValueObject;
import org.ccci.util.strings.Strings;
import org.hibernate.validator.Length;

import com.google.common.base.Preconditions;

/**
 * Value object representing a valid 9 digit employee id (with a possible 'S' suffix).
 * 
 * 
 * May be embedded in a JPA entity.
 * 
 * @author Matt Drees
 *
 */
//TODO: make this a value object, but with a hibernate user-type 
@Embeddable
public class EmployeeId extends ValueObject implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @Length(max=10, min=9)
    private String employeeId;

    //for JPA
    protected EmployeeId()
    {
    }
    
    protected EmployeeId(String employeeId)
    {
        this.employeeId = employeeId;
    }
    
    public String getEmployeeId()
    {
        return employeeId;
    }
    
    @Override
    public String toString()
    {
        return employeeId;
    }

    /**
     * If the given {@code employeeId} can be coerced into an {@link EmployeeId}, this method returns it. 
     * Otherwise, returns null.
     * 
     * Example Strings that can coerced:
     * "123456789"
     * "123456789S"
     * "123456789s"
     * " 123456789S "
     * "42"
     * "42S"
     * 
     * Example Strings that can't:
     * "abcdf"
     * ""
     * "123456789 123456789"
     * "42 42"
     * 
     * @param string may be null
     * @return
     */
    @ConstructsFromString
    public static EmployeeId coerce(String string)
    {
        if (string == null) return null;
        if (Strings.isEmpty(string)) return null;
        Matcher matcher = Pattern.compile("[0-9]{1,9}[Ss]?").matcher(string); 
        if (!matcher.find())
        {
            return null;
        }
        String candidate = matcher.group();
        if (matcher.find())
        {
            //more than one employee id in the string, so safer to return null instead of picking one
            return null;
        }
        return EmployeeId.valueOf(correctLength(correctSuffix(candidate)));
    }
    
    private static String correctSuffix(String candidate)
    {
        return candidate.replace('s', 'S');
    }

    private static String correctLength(String candidateWithCorrectSuffix)
    {
        int requiredLength = candidateWithCorrectSuffix.endsWith("S") ? 10 : 9;
        return Strings.leftPad(candidateWithCorrectSuffix, requiredLength, '0');
    }

    public static EmployeeId valueOf(String employeeId)
    {
        validate(employeeId);
        return new EmployeeId(employeeId);
    }

    public static EmployeeId nullableValueOf(String employeeId)
    {
        return employeeId == null ? null : valueOf(employeeId);
    }
    
    private static final Pattern EMPLOYEE_ID_PATTERN = Pattern.compile("[0-9]{9}[S]?");
    
    public static boolean isValidEmployeeId(String candidateEmployeeId)
    {
        return candidateEmployeeId != null && EMPLOYEE_ID_PATTERN.matcher(candidateEmployeeId).matches();
    }
    
    private static void validate(String employeeId)
    {
        Preconditions.checkNotNull(employeeId, "employeeId is null");
        Preconditions.checkArgument(!Strings.isEmpty(employeeId), "employeeId is empty");
        Preconditions.checkArgument(EMPLOYEE_ID_PATTERN.matcher(employeeId).matches(), "employeeId is invalid: " + employeeId);
    }

	@Override
	protected Object[] getComponents() {
		return new Object[]{employeeId};
	}
	
	/**
	 * Verify data in DB is usable.
	 */
	@PostLoad
	protected void verifyEmployeeIdValid()
	{
	    validate(employeeId);
	}

	/**
	 * Returns true if this employee id does not end with an "S" suffix
	 * @return
	 */
    public boolean isPrimaryId()
    {
        return !employeeId.endsWith("S");
    }

    /**
     * If this is a primary employee id, return this; otherwise, return this id without the "S" suffix.
     * The returned id may or may not correspond to an actual employee.
     */
    public EmployeeId getPrimaryId()
    {
        if (isPrimaryId())
        {
            return this;
        }
        else
        {
            return valueOf(employeeId.substring(0, 9));
        }
    }
    
    /**
     * If this is a secondary employee id, return this; otherwise, return this id suffixed with "S".
     * The returned id may or may not correspond to an actual employee.
     */
    public EmployeeId getSpouseId()
    {
        if (!isPrimaryId())
        {
            return this;
        }
        else
        {
            return valueOf(employeeId + "S");
        }
    }
    
}
