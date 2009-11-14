package org.ccci.util.provider;

import static org.jboss.seam.ScopeType.EVENT;

import org.ccci.auth.CasAuthenticator;
import org.ccci.auth.CcciCredentials;
import org.ccci.model.EmployeeId;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Identity;

import com.google.common.base.Preconditions;

/**
 * Requires that CcciSeamIdentity is installed.
 * @author Matt Drees
 *
 */
@Name("loggedInEmployeeIdProvider")
public class LoggedInEmployeeIdProvider
{

    @In Identity identity;
    
    @In CcciCredentials credentials;
    
    /**
     * Requires that the ccci-specific cas attributes have been populated
     * by the authenticator (see, e.g. {@link CasAuthenticator#authenticate()})
     * 
     * @return null if the user is not logged in
     */
    @Factory(value = "loggedInEmployeeId", autoCreate = true, scope = EVENT)
    public EmployeeId provideLoggedInEmployeeId()
    {
        if (identity.isLoggedIn())
        {
            EmployeeId emplid = credentials.getEmployeeId();
            Preconditions.checkState(emplid != null, "emplid was not provided during CAS authentication for %s!",
                credentials.getUsername());
            return emplid;
        }
        return null;
    }
    
    /**
     * User must be logged in
     * @return true if the user's CAS account has an employee id associated
     */
    public boolean isEmployeeIdAssociatedWithCasAccount()
    {
        Preconditions.checkState(identity.isLoggedIn(), "user is not logged in!");
        EmployeeId emplid = credentials.getEmployeeId();
        return emplid != null;
    }
}
