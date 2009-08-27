package org.ccci.auth;

import static org.jboss.seam.ScopeType.STATELESS;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

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
    @Logger
    Log log;

    @In
    CcciIdentity identity;

    @In
    FacesContext facesContext;

    @In
    Context sessionContext;

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
                log.error("CASReceipt is not available in httpSession");
                return false;
            }
            else
            {
                identity.setUsername(casReceipt.getUserName().toLowerCase());
                //CASReceipt api doesn't show it, but keys/attributes are always Strings
                @SuppressWarnings("unchecked")
                Map<String, String> attributes = casReceipt.getAttributes();
                identity.setAttributes(attributes);
                
                log.debug("emplid: #0", identity.getAttributes().get("emplid"));
                
                return true;
            }
        }
        catch (RuntimeException systemException)
        {
            identity.notifyAuthenticationSystemException(systemException);
            throw systemException;
        }
    }

}
