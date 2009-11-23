package org.ccci.testutils;

import java.util.List;

import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class StatusMessagesStub extends StatusMessages
{
    @Override
    public List<StatusMessage> getMessages()
    {
        doRunTasks();
        return super.getMessages();
    }
    
    public boolean hasErrorMessage()
    {
        return !getMessages(Severity.ERROR).isEmpty();
    }

    public List<StatusMessage> getMessages(Severity severity)
    {
        List<StatusMessage> errorMessages = Lists.newArrayList();
        for (StatusMessage message :  getMessages())
        {
            if (message.getSeverity() == severity)
            {
                errorMessages.add(message);
            }
        }
        return errorMessages;
    }

}