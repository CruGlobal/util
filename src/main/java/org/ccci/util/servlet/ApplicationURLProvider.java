package org.ccci.util.servlet;

import static org.jboss.seam.ScopeType.STATELESS;

import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.ccci.util.Exceptions;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.mock.MockFacesContext;

import com.google.common.base.Preconditions;

/**
 * Provides a factory method for getting the application's url, which can be used to build
 * links. Will include up to the context path, with no trailing slash.
 *   
 * E.g,  "http://staff2.ccci.org/eTimesheet"
 * 
 * The base part of the url is specified via a System Property (org.ccci.serverUrl), and the context
 * path is obtained from the servletContext.
 * 
 * applicationUrl will be constructed at most once per httpRequest.
 * 
 * I used to try to construct the applicationUrl from the current FacesContext if it was available, but
 * sometimes the FacesContext is unreliable.  (In particular, seam facelets email sets up a MailFacesContextImpl,
 * which may or may not be backed by an unusable {@link MockFacesContext})
 * 
 * @author Matt Drees
 *
 */
@Name("applicationURLProvider")
@Scope(STATELESS)
@AutoCreate
public class ApplicationURLProvider
{

    public static final String SERVER_URL_KEY = "org.ccci.serverUrl";

    @In(required = false) FacesContext facesContext;

    //Configured in web.xml
    private String facesServletExtensionMapping;
    
    @Factory(value = "facesServletExtension", autoCreate = true)
    public String provideFacesServletExtension()
    {
    	return facesServletExtensionMapping;
    }
    
    @Factory(value = "applicationURL", autoCreate = true)
    public String provideApplicationURL() {
    	String serverUrl = System.getProperty(SERVER_URL_KEY);
    	Preconditions.checkState(
            serverUrl != null,
            "System Properties do not contain a property for key %s;" +
            " please start JVM with -D%s={serverUrl} where serverUrl is e.g. http://localhost:8080",
            SERVER_URL_KEY, SERVER_URL_KEY);
        Preconditions.checkArgument(!serverUrl.endsWith("/"),
            "serverUrl %s specified with system property %s should not end with a slash", serverUrl, SERVER_URL_KEY);
        try
        {
            new URL(serverUrl);
        }
        catch (MalformedURLException e)
        {
            throw Exceptions.newIllegalArgumentException(e,
                "serverUrl specified with system property %s is not a valid URL", SERVER_URL_KEY);
        }
    	
		return serverUrl + getContextPath();
	}

	private String getContextPath() {
		ServletContext servletContext = ServletLifecycle.getServletContext();
		return servletContext == null ? "" : 
		    servletContext.getContextPath() == null ? "" : servletContext.getContextPath();
	}

    public void setFacesServletExtensionMapping(String facesServletExtensionMapping)
    {
        Preconditions.checkArgument(!facesServletExtensionMapping.contains("."),
            "invalid facesServletExtensionMapping; shouldn't contain '.' (%s)", facesServletExtensionMapping);
        this.facesServletExtensionMapping = facesServletExtensionMapping;
    }

	public String getFacesServletExtensionMapping() {
		return facesServletExtensionMapping;
	}
}
