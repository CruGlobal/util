package org.ccci.auth;

import static org.jboss.seam.ScopeType.STATELESS;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.ccci.model.Designation;
import org.ccci.model.EmployeeId;
import org.ccci.model.SsoGuid;
import org.ccci.model.SsoUsername;
import org.ccci.util.strings.Strings;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesManager;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

/**
 * If you don't want to hit the real CAS, (if you're offline, for example),
 * configure this in components.xml:
 * 
 *   <ccci-auth:mock-authenticator
 *     username="whatever.you@want.com"
 *     emplid="000123456"
 *     designation="0123456"
 *     ssoGuid="BB20A5DB-D31E-65B5-3629-E24504A00942"
 *     fakeLogoutView="/pageInsteadOfCasLogoutPage.xhtml"/>
 * 
 * also make sure you import the following namespace in the root xml element:
 *   xmlns:ccci-auth="urn:java:org.ccci.auth"
 * 
 * @author Nathan Kopp
 * @author Matt Drees
 *
 */
@Name("casAuthenticator")
@Scope(STATELESS)
@Install(value = false, precedence = Install.MOCK)
public class MockAuthenticator implements Authenticator
{
    private SsoUsername username;
    private EmployeeId emplid;
    private Designation designation;
    private SsoGuid ssoGuid;
    
    private String fakeLogoutView;
    
    @Logger
    Log log;
    
    @In
    CcciCredentials credentials;
    
    @In
    FacesContext facesContext;

    @In
    FacesMessages facesMessages;
    
    @In
    FacesManager manager;
    
    @Create
    public void validate()
    {
        if (username == null) {
            throw new IllegalStateException("Please configure username for ccci-auth:mock-authenticator in components.xml");
        }
    }
    
    public void casLogout() throws IOException
    {
        if (fakeLogoutView == null) {
            throw new IllegalStateException("Please configure fakeLogoutView for mock casAuthenticator in components.xml");
        }
        manager.redirect(fakeLogoutView);
    }
    
    public boolean authenticate()
    {
        credentials.setUsername(username.toString());
        credentials.setEmployeeId(emplid);
        credentials.setDesignation(designation);
        credentials.setSsoGuid(ssoGuid);
        return true;
    }

    public String getUsername()
    {
        return Strings.safeToString(username);
    }

    public void setUsername(String username)
    {
        this.username = new SsoUsername(username);
    }

    public String getEmplid()
    {
        return Strings.safeToString(emplid);
    }

    public void setEmplid(String emplid)
    {
        this.emplid = EmployeeId.valueOf(emplid);
    }

    public String getDesignation()
    {
        return Strings.safeToString(designation);
    }

    public void setDesignation(String designation)
    {
        this.designation = new Designation(designation);
    }

    public String getFakeLogoutView()
    {
        return fakeLogoutView;
    }

    public void setFakeLogoutView(String fakeLogoutView)
    {
        this.fakeLogoutView = fakeLogoutView;
    }
    
    public void setSsoGuid(String ssoGuid)
    {
        this.ssoGuid = new SsoGuid(ssoGuid);
    }
    
    public String getSsoGuid()
    {
        return Strings.safeToString(ssoGuid);
    }
}
