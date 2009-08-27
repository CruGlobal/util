package org.ccci.util.mail;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.ccci.util.Exceptions;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * 
 * Simple API for emails. Sends a separate email to each person that has been added via
 * {@link #addTo(String, String)}.
 * Adapted from old framework jar. 
 * Use by injecting a {@link MailMessageFactory}, and calling {@link MailMessageFactory#createApplicationMessage()}.
 * 
 * Should only be used for simple, plain text emails. HTML emails should use Seam / facelets email templating (see
 * http://docs.jboss.com/seam/2.0.2.GA/reference/en-US/html/mail.html )
 * 
 * 
 * Not threadsafe.
 * @author Matt Drees
 */
public class MailMessage 
{
    private Session session;
    private Message message;

    private Log log = Logging.getLog(MailMessage.class);
    
    private List<InternetAddress> toList = Lists.newArrayList();

    private boolean sent = false;
    
    /** whether this message is an application message, or a system message */
    private final boolean system;
    
    MailMessage(Session session, boolean system)
    {
        this.session = session;
        message = new MimeMessage(session);
        this.system = system;
    }

    /**
     * Add a person to the list of recipients, using a personal name
     * @throws UnsupportedEncodingException 
     */
    public void addTo(EmailAddress address, String name) 
    {
        checkNotSent();
        Preconditions.checkNotNull(address, "address is null");
        Preconditions.checkNotNull(name, "name is null");
        toList.add(address.toInternetAddress(name));
    }

    private void checkNotSent()
    {
        Preconditions.checkState(!sent, "already sent message!");
    }
    

    /**
     * Add a person to the list of recipients
     * @throws UnsupportedEncodingException 
     */
    public void addTo(EmailAddress address)
    {
        checkNotSent();
        Preconditions.checkNotNull(address, "address is null");
        toList.add(address.toInternetAddress());
    }
    
    
    /**
     * set the "from" address and name
     */
    public void setFrom(EmailAddress address, String name)
    {
        checkNotSent();
        Preconditions.checkNotNull(address, "address is null");
        Preconditions.checkNotNull(name, "name is null");
        try
        {
            message.setFrom(address.toInternetAddress(name));
        }
        catch (MessagingException e)
        {
            throw Exceptions.wrap(e);
        }
    }
    

    public void setFrom(EmailAddress address)
    {
        checkNotSent();
        Preconditions.checkNotNull(address, "address is null");
        try
        {
            message.setFrom(address.toInternetAddress());
        }
        catch (MessagingException e)
        {
            throw Exceptions.wrap(e);
        }
    }

    /**
     * set the reply-to address and name
     */    
    public void setReplyTo(EmailAddress address, String name)
    {
        checkNotSent();
        Preconditions.checkNotNull(address, "address is null");
        Preconditions.checkNotNull(name, "name is null");
        InternetAddress addresses[] = new InternetAddress[1];
        addresses[0] = address.toInternetAddress(name);
        try
        {
            message.setReplyTo(addresses);
        }
        catch (MessagingException e)
        {
            throw Exceptions.wrap(e);
        }
    }

    /**
     * Set the subject and text message
     */
    public void setMessage(String subject, String body)
    {
        checkNotSent();
        Preconditions.checkNotNull(subject, "subject is null");
        Preconditions.checkNotNull(body, "body is null");
        try
        {
            message.setSubject(subject);
            message.setContent(body, "text/plain");
        }
        catch (MessagingException e)
        {
            throw Exceptions.wrap(e);
        }
    }

    /**
     * send the message to each recipient individually
     * @throws SendFailedException - if the message could not be sent to one of the recipients.  
     * @throws MessagingException
     */    
    public void sendToEach() throws MessagingException 
    {
        checkNotSent();
        setJavaGeneratedHeader();
        
        Transport transport = session.getTransport();
        transport.connect();
        try
        {
            for (InternetAddress address : toList)
            {
                message.setRecipient(Message.RecipientType.TO, address);
                message.saveChanges();
                send(transport, message);
                log.debug("sent to #0", address);
            }
        }
        finally
        {
            transport.close();
        }
        sent = true;
    }

    private void send(Transport transport, Message message) throws MessagingException
    {
        if (system && transport instanceof IndirectingSMTPTransport)
        {
            ((IndirectingSMTPTransport)transport).sendMessageWithoutIndirection(message, message.getAllRecipients());
        }
        else
        {
            transport.sendMessage(message, message.getAllRecipients());
        }
    }
    
    /**
     * send the message to all recipients
     * @throws SendFailedException - if the message could not be sent to one of the recipients.  
     * @throws MessagingException
     */    
    public void sendToAll() throws MessagingException 
    {
        checkNotSent();
        setJavaGeneratedHeader();
        
        Transport transport = session.getTransport();
        transport.connect();
        try
        {
            for (InternetAddress address : toList)
            {
                message.addRecipient(Message.RecipientType.TO, address);
            }
            message.saveChanges();
            send(transport, message);
            log.debug("sent to #0", toList);
        }
        finally
        {
            transport.close();
        }
        sent = true;
    }

    /**
     * important - set this so that our SMTP server
     * won't mess with this message
     */
    private void setJavaGeneratedHeader() throws MessagingException
    {
        message.setHeader("X-JavaGenerated","Auto");
    }


}
