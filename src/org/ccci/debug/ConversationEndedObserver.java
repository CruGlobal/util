/**
 * 
 */
package org.ccci.debug;

import java.io.Serializable;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.core.Manager;

@Name("conversationEndedObserver")
public class ConversationEndedObserver implements Serializable
{
    
    private static final long serialVersionUID = 1L;

    @In Manager manager;
    
    private boolean exceptionRecoveryInProgress;
    private boolean conversationWasEndedByException;
    private String endedConversationId;
    
    @Observer("org.jboss.seam.endConversation")
    public void conversationEnded()
    {
        if (exceptionRecoveryInProgress)
        {
            conversationWasEndedByException = true;
            endedConversationId = manager.getCurrentConversationId();
        }
    }

    public boolean wasConversationEndedByException()
    {
        return conversationWasEndedByException;
    }

    public String getEndedConversationId()
    {
        return endedConversationId;
    }

    public void beginExceptionRecovery()
    {
        exceptionRecoveryInProgress = true;
    }
}