package org.ccci.util.mail;

import javax.mail.Session;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Used by client code to create {@link MailMessage}s
 * 
 * @author Matt Drees
 */
@Name("mailMessageFactory")
@Scope(ScopeType.STATELESS)
@AutoCreate
public class MailMessageFactory
{

    @In Session mailSession;

    /**
     * Creates a {@link MailMessage} suitable for application/business email messages.
     * These emails might be indirected by {@link MailIndirection}, if configured.
     */
    public MailMessage createApplicationMessage()
    {
        return new MailMessage(mailSession, false);
    }
    
    /**
     * Creates a {@link MailMessage} suitable for system/sysadmin email messages.  For example,
     * used by exception email reports.  These should be guaranteed to be delivered to the emailaddress,
     * even if {@link MailIndirection} is configured.
     */
    public MailMessage createSystemMessage()
    {
        return new MailMessage(mailSession, true);
    }
}
