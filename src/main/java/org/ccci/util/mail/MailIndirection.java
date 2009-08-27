package org.ccci.util.mail;

import static org.jboss.seam.ScopeType.APPLICATION;

import org.ccci.util.seam.Components;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Configure in components.xml:
 * 
 *  <ccci-mail:mail-indirection
 *     replacementEmailAddress="whatever.you@want.com"/>
 * 
 * also make sure you import the following namespace in the root xml element:
 *   xmlns:ccci-mail="urn:java:org.ccci.util.mail"
 *   
 * @author Matt Drees
 *
 */
@Name("mailIndirection")
@Scope(APPLICATION)
@Install(value = false)
public class MailIndirection
{
    private EmailAddress replacementEmailAddress;
    
    public void setReplacementEmailAddress(String replacementEmailAddress)
    {
        this.replacementEmailAddress = EmailAddress.valueOf(replacementEmailAddress);
    }

    public EmailAddress getReplacementEmailAddress()
    {
        return replacementEmailAddress;
    }

    public boolean isConfigured()
    {
        return replacementEmailAddress != null;
    }
    
    public static MailIndirection instance()
    {
        return Components.lookup(MailIndirection.class);
    }

}
