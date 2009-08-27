package org.ccci.debug;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.google.common.collect.Lists;

@Name("exceptionHistory")
@Scope(SESSION)
@BypassInterceptors
/*
 *   @Startup because exception handling code depends on its existence, possibly outside the boundaries of the sessionContext, 
 *   and I didn't want to have to do any initialization logic then
 */
@Startup
public class ExceptionHistory implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final List<String> conversationIdsKilledByExceptions = Lists.newArrayList();

    public void addConversationIdKilledByException(String conversationId)
    {
        conversationIdsKilledByExceptions.add(conversationId);
    }

    public static ExceptionHistory instance()
    {
        return (ExceptionHistory) Component.getInstance(ExceptionHistory.class);
    }

    public boolean wasConversationKilledByException(String conversationId)
    {
        return conversationIdsKilledByExceptions.contains(conversationId);
    }

    public List<String> getConversationIdsKilledByExceptions()
    {
        return conversationIdsKilledByExceptions;
    }

}
