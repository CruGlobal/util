package org.ccci.auth;

import static org.jboss.seam.ScopeType.STATELESS;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesManager;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;

import com.google.common.collect.Maps;

/**
 * If you don't want to hit the real CAS, (if you're offline, for example),
 * configure this in components.xml:
 * 
 *   <ccci-auth:mock-authenticator
 *     username="whatever.you@want.com"
 *     emplid="000123456"
 *     designation="0123456"
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
    private String username;
    private String password = "password";
    private String emplid;
    private String designation;
    
    private String fakeLogoutView;
    
    @Logger
    Log log;

    @In
    CcciIdentity identity;
    
    @In
    Credentials credentials;
    
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
        credentials.setUsername(username);
        credentials.setPassword(password);
        Map<String, String> attributes = Maps.newHashMap();
        if (emplid != null)
        {
            attributes.put("emplid", emplid);
        }
        if (designation != null)
        {
            attributes.put("designation", designation);
        }
        identity.setAttributes(attributes);
        return true;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getEmplid()
    {
        return emplid;
    }

    public void setEmplid(String emplid)
    {
        this.emplid = emplid;
    }

    public String getDesignation()
    {
        return designation;
    }

    public void setDesignation(String designation)
    {
        this.designation = designation;
    }

    public String getFakeLogoutView()
    {
        return fakeLogoutView;
    }

    public void setFakeLogoutView(String fakeLogoutView)
    {
        this.fakeLogoutView = fakeLogoutView;
    }
    
}
