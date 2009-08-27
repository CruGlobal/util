package org.ccci.ss;

import static org.jboss.seam.ScopeType.STATELESS;

import org.ccci.model.Employee;
import org.ccci.model.EmploymentStatus;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.joda.time.Days;
import org.joda.time.LocalDate;

@Name("terminationRules")
@Scope(STATELESS)
public class TerminationRules
{

    public boolean isTerminated(Employee employee)
    {
        String employmentStatus = employee.getEmploymentStatus();
        return EmploymentStatus.TERMINATED.equals(employmentStatus);
    }
    
    public boolean terminatedPastGracePeriod(Employee employee, LocalDate today)
    {
        if (!isTerminated(employee)) return false;
        int days = Days.daysBetween(employee.getTerminationDate(), today).getDays();
        if (employee.isIntern() && days > 30) return true;
        if (days > 90) return true;
        return false;
    }


}
