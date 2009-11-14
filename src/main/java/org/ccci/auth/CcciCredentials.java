package org.ccci.auth;

import static org.jboss.seam.ScopeType.SESSION;

import org.ccci.model.Designation;
import org.ccci.model.EmployeeId;
import org.ccci.model.PeopleId;
import org.ccci.model.SsoGuid;
import org.ccci.model.SsoUsername;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.security.Credentials;

@Name("org.jboss.seam.security.credentials")
@Scope(SESSION)
@Install(precedence = Install.FRAMEWORK)
@BypassInterceptors
public class CcciCredentials extends Credentials
{
    
    private SsoGuid ssoGuid;
    private EmployeeId employeeId;
    private Designation designation;
    private PeopleId peopleId;

    
    @Override
    public void setPassword(String password)
    {
        if (password != null)
        {
            throw new UnsupportedOperationException("passwords are not applicable in a cas-protected application");
        }
    }
    
    
    public SsoUsername getSsoUsername()
    {
        return getUsername() == null ? null : new SsoUsername(getUsername());
    }
    
    public void setSsoUsername(SsoUsername ssoUsername)
    {
        setUsername(ssoUsername == null ? null : ssoUsername.toString());
    }
    
    public SsoGuid getSsoGuid()
    {
        return ssoGuid;
    }
    
    public void setSsoGuid(SsoGuid ssoGuid)
    {
        this.ssoGuid = ssoGuid;
    }
    
    public EmployeeId getEmployeeId()
    {
        return employeeId;
    }

    public void setEmployeeId(EmployeeId employeeId)
    {
        this.employeeId = employeeId;
    }

    public Designation getDesignation()
    {
        return designation;
    }

    public void setDesignation(Designation designation)
    {
        this.designation = designation;
    }

    public PeopleId getPeopleId()
    {
        return peopleId;
    }

    public void setPeopleId(PeopleId peopleId)
    {
        this.peopleId = peopleId;
    }

    private static final long serialVersionUID = 1L;
}
