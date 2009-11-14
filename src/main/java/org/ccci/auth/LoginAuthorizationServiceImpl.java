package org.ccci.auth;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * A default implementation of {@link LoginAuthorizationService} that allows everyone in.
 * 
 * @author Matt Drees
 */
@Name("loginAuthorizationService")
@Install(precedence = Install.FRAMEWORK)
@AutoCreate
@Scope(ScopeType.STATELESS)
public class LoginAuthorizationServiceImpl implements LoginAuthorizationService
{

    /**
     * By default, all successful cas logins are authorized.
     * @return
     */
    public boolean loginAuthorized()
    {
        return true;
    }
}
