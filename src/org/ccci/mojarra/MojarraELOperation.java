package org.ccci.mojarra;

import javax.servlet.ServletContext;

import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.google.common.base.Preconditions;
import com.sun.faces.application.ApplicationAssociate;

/**
 * Binds Mojarra's ApplicationAssociate to this thread for the duration of the method, to avoid an NPE in async
 * mail
 * 
 * Basically, a workaround for problem described at https://jira.jboss.org/jira/browse/JBSEAM-3187
 * 
 * Only needed for mojarra 1.8 & 1.9. Jboss 4.2.3.ga's default jsf impl is 1.8
 * 
 * @author Matt Drees
 *
 */
public abstract class MojarraELOperation 
{
    Log log = Logging.getLog(MojarraELOperation.class);
    
    protected abstract void work();
    
    public void workEnsuringMojarraELAvailable()
    {
        final boolean associated;
        if(ApplicationAssociate.getCurrentInstance() == null) {
            ServletContext servletContext = Preconditions.checkNotNull(
                ServletLifecycle.getServletContext(), 
                "can't get ServletContext");
            ApplicationAssociate associate = Preconditions.checkNotNull(
                ApplicationAssociate.getInstance(servletContext), 
                "can't get ApplicationAssociate from ServletContext");
            ApplicationAssociate.setCurrentInstance(associate);
            associated = true;
        }
        else
        {
            associated = false;
        }
        try
        {
            work();
        }
        finally
        {
            if (associated) ApplicationAssociate.setCurrentInstance(null);
        }
    }
    
}
