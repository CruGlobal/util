package org.ccci.model;

import java.util.Iterator;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class EmployeeCouple implements EmployeeUnit
{

    private final Employee primary;
    private final Employee secondary;

    public EmployeeCouple(Employee primary, Employee secondary)
    {
        Preconditions.checkArgument(atLeastOneIs(primary, secondary, Gender.M), "neither %s nor %s is a male", primary, secondary);
        Preconditions.checkArgument(atLeastOneIs(primary, secondary, Gender.F), "neither %s nor %s is a female", primary, secondary);
        Preconditions.checkArgument(primary.getEmployeeId().isPrimaryId(), "primary employee %s does not have a primary employee id", primary);
        Preconditions.checkArgument(!secondary.getEmployeeId().isPrimaryId(), "secondary employee %s has a primary employee id", secondary);
        this.primary = primary;
        this.secondary = secondary;
    }

    private boolean atLeastOneIs(Employee one, Employee other, Gender gender)
    {
        return one.getGender() == gender || other.getGender() == gender;
    }

    @Override
    public String getName()
    {
        return getHusband().getFirstName() + " and " + getWife().getFirstName() + " " + getHusband().getLastName();   
    }

    @Override
    public boolean isCouple()
    {
        return true;
    }

    @Override
    public Employee getPrimary()
    {
        return primary;
    }

    public Employee getSecondary()
    {
        return secondary;
    }

    public Employee getHusband()
    {
        if (primary.getGender() == Gender.M)
        {
            return primary;
        }
        else
        {
            return secondary;
        }
    }
    
    public Employee getWife()
    {
        if (primary.getGender() == Gender.F)
        {
            return primary;
        }
        else
        {
            return secondary;
        }
    }
    
    @Override
    public Iterator<Employee> iterator()
    {
        return ImmutableList.of(primary, secondary).iterator();
    }
    
    private static final long serialVersionUID = 1L;
}
