package org.ccci.util.mail;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.sun.mail.smtp.SMTPTransport;

/**
 * For Q/A testing, we don't want to send application emails to the email addresses in the database, since those are real people's
 * addresses.  Instead, send all emails to a configured address, so that the email system can be verified without bugging
 * the real address owners.  
 * 
 * For system emails (such as exception reports), the client should cast the Session's Transport object to {@link IndirectingSMTPTransport},
 * and then call {@link #sendMessageWithoutIndirection(MimeMessage, Address[])}
 * 
 * 
 * Only used if {@link MailIndirection} is configured.
 * 
 * Only the email address portion itself is changed; the "personal name" part of the address is kept.
 * 
 * This transport must be registered in META-INF/javamail.providers to be useful
 * 
 * @author Matt Drees
 */
public class IndirectingSMTPTransport extends SMTPTransport
{

    Log log = Logging.getLog(IndirectingSMTPTransport.class);
    
    public IndirectingSMTPTransport(Session session, URLName urlname)
    {
        super(session, urlname);
    }

    public void sendMessageWithoutIndirection(Message message, Address[] allRecipients) throws SendFailedException, MessagingException
    {
        log.debug("sending SMTP message to recipients, bypassing indirection");
        super.sendMessage(message, allRecipients);
    }
    
    @Override
    public void sendMessage(Message message, Address[] addresses) throws MessagingException
    {
        MailIndirection mailIndirection = MailIndirection.instance();
        if (mailIndirection == null || !mailIndirection.isConfigured())
        {
            log.debug("indirection not configured; sending SMTP message to recipients");
            super.sendMessage(message, addresses);
            return;
        }
        
        EmailAddress replacement = mailIndirection.getReplacementEmailAddress();
        List<Address> newAddresses = new ArrayList<Address>(addresses.length);
        log.debug("indirecting SMTP message to #0", replacement);
        for (Address address : addresses)
        {
            if (address instanceof InternetAddress)
            {
                InternetAddress internetAddress = (InternetAddress) address;
                newAddresses.add(replacement.toInternetAddress(internetAddress.getPersonal()));
            }
            else
            {
                newAddresses.add(replacement.toInternetAddress()); 
            }
        }
        super.sendMessage(message, newAddresses.toArray(new Address[newAddresses.size()]));
    }

}
