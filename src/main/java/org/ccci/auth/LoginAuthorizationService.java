package org.ccci.auth;

import org.ccci.model.EmployeeId;

/**
 * Used by {@link CasAuthenticator} to restrict user authentications.
 * 
 * @author Matt Drees
 */
public interface LoginAuthorizationService
{

    /**
     * Returns true if the user identified by the current {@link CcciCredentials} should be allowed to log in.
     * This could check for 'enabled' flags or verify that the user has been given an {@link EmployeeId}.
     * 
     * This method should also add any necessary status messages to inform the user why they have not been authenticated.
     */
    public abstract boolean loginAuthorized();

}
