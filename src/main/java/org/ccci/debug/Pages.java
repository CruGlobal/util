package org.ccci.debug;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.ConversationPropagation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Name("org.jboss.seam.navigation.pages")
@Startup
@Install(precedence=Install.FRAMEWORK, classDependencies="javax.faces.context.FacesContext")
public class Pages extends org.jboss.seam.navigation.Pages
{

    Log log = Logging.getLog(Pages.class);
    @Override
    public void redirectToNoConversationView()
    {
        //first check if the requested conversation was ended by an exception
        ExceptionHistory exceptionHistory = ExceptionHistory.instance();
        String conversationId = ConversationPropagation.instance().getConversationId();
        if (exceptionHistory.wasConversationKilledByException(conversationId))
        {
            log.warn("requested conversation (#0) was ended by a previous exception", conversationId);
            redirectToErrorView();
        }
        else
        {
            if (conversationId != null)
            {
                log.debug("conversation required, but requested conversation (#0) does not exist", conversationId);
            } 
            else 
            {
                log.debug("conversation required, but no conversation id was given");
            }
            super.redirectToNoConversationView();
        }
    }

    private void redirectToErrorView()
    {
        Manager.instance().redirect("/error.xhtml");
    }
}
