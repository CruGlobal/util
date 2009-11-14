package org.ccci.auth;

import static org.jboss.seam.ScopeType.STATELESS;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.ccci.model.Designation;
import org.ccci.model.EmployeeId;
import org.ccci.model.PeopleId;
import org.ccci.model.SsoGuid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.log.Log;

import com.google.common.base.Preconditions;

import edu.yale.its.tp.cas.client.CASReceipt;
import edu.yale.its.tp.cas.client.filter.CASFilter;

/**
 * An authenticator that simply expects authentication to have been performed by a servlet filter.  The user's credentials
 * are retrieved from a location in the session identified by {@link CASFilter.CAS_FILTER_RECEIPT}.
 * 
 * To use, set 
 *  <security:identity authenticate-method="#{casAuthenticator.authenticate}"/>
 * in components.xml.
 * 
 * In addition, for logouts, also add
 * 
 *  <event type="org.jboss.seam.security.loggedOut">
 *    <action execute="#{casAuthenticator.casLogout}" />
 *  </event>
 *  
 * some inspiration from
 * http://www.ja-sig.org/wiki/display/CASC/Seam+Identity+Integration+(Seam+1.2.1+-+2.0.0)
 * 
 * @author Nathan Kopp
 * @author Matt Drees
 */
@Name("casAuthenticator")
@Scope(STATELESS)
@Install(precedence = Install.FRAMEWORK)
public class CasAuthenticator implements Authenticator
{
    public static final String EMPLID_ATTRIBUTE = "emplid";
    public static final String PEOPLE_ID_ATTRIBUTE = "peopleid";
    public static final String DESIGNATION_ATTRIBUTE = "designation";
    public static final String SSO_GUID_ATTRIBUTE = "ssoGuid";

    @Logger
    Log log;

    @In
    SystemExceptionRecordingIdentity identity;

    @In
    CcciCredentials credentials;
    
    @In
    FacesContext facesContext;

    @In
    Context sessionContext;

    @In
    LoginAuthorizationService loginAuthorizationService; 
    
    
    public void casLogout() throws IOException
    {
        ExternalContext externalContext = facesContext.getExternalContext();
        String casServerLogoutUrl = externalContext.getInitParameter("cas.logoutUrl");
        Preconditions.checkState(casServerLogoutUrl != null, "cas logoutUrl not found");
        ((HttpServletResponse)externalContext.getResponse()).sendRedirect(casServerLogoutUrl);
        facesContext.responseComplete();
    }

    public boolean authenticate()
    {
        try
        {
            CASReceipt casReceipt = (CASReceipt) sessionContext.get(CASFilter.CAS_FILTER_RECEIPT);
            
            if (casReceipt == null)
            {
                throw new IllegalStateException("CASReceipt is not available in httpSession");
            }
            else
            {
                credentials.setUsername(casReceipt.getUserName().toLowerCase());
                //CASReceipt api doesn't show it, but keys/attributes are always Strings
                @SuppressWarnings("unchecked")
                Map<String, String> attributes = casReceipt.getAttributes();
                String guid = attributes.get(SSO_GUID_ATTRIBUTE);
                if (guid == null)
                {
                    throw new IllegalStateException("CASReceipt did not contain ssoGuid");
                }
                credentials.setSsoGuid(new SsoGuid(guid));
                credentials.setDesignation(attributes.get(DESIGNATION_ATTRIBUTE) == null ? null : 
                    new Designation(attributes.get(DESIGNATION_ATTRIBUTE)));
                credentials.setPeopleId(attributes.get(PEOPLE_ID_ATTRIBUTE) == null ? null :
                    new PeopleId(attributes.get(PEOPLE_ID_ATTRIBUTE)));
                credentials.setEmployeeId(attributes.get(EMPLID_ATTRIBUTE) == null ? null :
                    EmployeeId.valueOf(attributes.get(EMPLID_ATTRIBUTE)));
                log.debug("authenticating; emplid: {0}, ssoGuid: {1}", credentials.getEmployeeId(), credentials.getSsoGuid());
                
                boolean loginAuthorized = loginAuthorizationService.loginAuthorized();
                if (!loginAuthorized)
                {
                    sessionContext.remove(CASFilter.CAS_FILTER_RECEIPT);
                }
                return loginAuthorized;
            }
        }
        catch (RuntimeException systemException)
        {
            identity.notifyAuthenticationSystemException(systemException);
            throw systemException;
        }
    }

}
