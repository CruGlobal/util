package org.ccci.util.provider;

import static org.jboss.seam.ScopeType.EVENT;

import org.ccci.auth.CasAuthenticator;
import org.ccci.auth.CcciIdentity;
import org.ccci.model.EmployeeId;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.util.Strings;

import com.google.common.base.Preconditions;

/**
 * Requires that CcciSeamIdentity is installed.
 * @author Matt Drees
 *
 */
@Name("loggedInEmployeeIdProvider")
public class LoggedInEmployeeIdProvider
{

    @In CcciIdentity identity;
    
    @In Credentials credentials;
    
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
            String emplid = identity.getAttributes().get("emplid");
            Preconditions.checkState(emplid != null, "emplid was not provided during CAS authentication for %s!",
                credentials.getUsername());
            return EmployeeId.valueOf(emplid);
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
        String emplid = identity.getAttributes().get("emplid");
        return !Strings.isEmpty(emplid);
    }
}
