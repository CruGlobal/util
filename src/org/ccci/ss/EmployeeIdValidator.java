package org.ccci.ss;

import org.ccci.dao.EmployeeDAO;
import org.ccci.dao.EmployeeNotFoundException;
import org.ccci.model.EmployeeId;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("employeeIdValidator")
public class EmployeeIdValidator
{

    @In EmployeeDAO employeeDAO;
    
    public boolean isValid(EmployeeId employeeId)
    {
        if (employeeId == null)
        {
            return false;
        }
        try 
        {
            employeeDAO.find(employeeId);
            return true;
        }
        catch(EmployeeNotFoundException e)
        {
            return false;
        }
    }
    
}
