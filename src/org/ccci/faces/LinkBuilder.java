package org.ccci.faces;

import javax.faces.application.ViewHandler;

import org.ccci.util.servlet.ApplicationURLProvider;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.google.common.base.Preconditions;

@Name("linkBuilder")
@Scope(ScopeType.STATELESS)
public class LinkBuilder
{
    
    @In String applicationURL;
    
    @In String facesServletExtension;

    /**
     * Builds a usable link for the given ViewId. Uses the applicationURL provided by
     * {@link ApplicationURLProvider}.
     * 
     * Some day, it might be nice to use {@link ViewHandler#getActionURL(javax.faces.context.FacesContext, String)}
     * to implement this, but it's more work to mock up the FacesContext if it's unavailable, and I'm not sure it
     * adds value.
     * 
     * @param viewId
     * @return
     */
    public String build(String viewId)
    {
        int index = viewId.indexOf(".xhtml");
        Preconditions.checkArgument(index > 0, "invalid viewId: %s.  Must match *.xhtml", viewId);
        String viewIdBody = viewId.substring(0, index);
        return applicationURL + "/" + viewIdBody + "." + facesServletExtension;
    }
}
